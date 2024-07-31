## Sharding-JDBC Demo: 水平分库 - HorizontalSubLibrary

**Desc:** Sharding-JDBC学习

**Author:** ZSS

**CreateTime:** 2021-02-24

---

> 水平分库：其实就是分库之后，还要进行水平分表
>
> 比如：数据库db_1下有表table_1，table_2；数据库db_2下有表table_1，table_2。。。

#### 1. application.yml配置文件

```yaml
spring:
  jpa:
    database: postgresql
    show-sql: true
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQL9Dialect
    open-in-view: false

  # sharding-jdbc配置
  shardingsphere:
    # 输出SQL日志
    props:
      sql:
        show: true
    datasource:
      # 数据源名称
      names: db1, db2
      # 配置数据源
      db1:
        # 第三方数据库连接池 - druid
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: org.postgresql.Driver
        url: jdbc:postgresql://127.0.0.1:5432/sharding_db_1
        username: sharding
        password: sharding
      db2:
        # 第三方数据库连接池 - druid
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: org.postgresql.Driver
        url: jdbc:postgresql://127.0.0.1:5432/sharding_db_2
        username: sharding
        password: sharding
    sharding:
      # 配置需要分表操作的表名
      tables:
        # 逻辑表名
        sjd_enterprise:
          # 配置数据节点，groovy表达式，db1或者db2，sjd_enterprise_1/sjd_enterprise_2
          actual-data-nodes: db$->{1..2}.sjd_enterprise_$->{1..2}
          # 分库策略，enterprise_id为偶数操作db1，为奇数操作db2
          database-strategy:
            inline:
              sharding-column: enterprise_id
              algorithm-expression: db$->{enterprise_id % 2 + 1}
          # 分表策略，enterprise_id为偶数操作sjd_enterprise_1，为奇数操作表sjd_enterprise_2
          table-strategy:
          	# 精准分片
            standard:
              # 指定表的分片策略：分片键
              sharding-column: enterprise_code
              # 指定表的分片策略：自定义分片算法
              precise-algorithm-class-name: com.zss.hsl.algorithm.UUIDShardingAlgorithm
        # 逻辑表名
        sjd_user:
          # 配置数据节点，groovy表达式，db1或者db2，sjd_user_1或sjd_user_2
          actual-data-nodes: db$->{1..2}.sjd_user_$->{1..2}
          # 分库策略
          database-strategy:
            inline:
              # 指定表的分片策略：分片键
              sharding-column: user_id
              # 指定表的分片策略：分片算法
              algorithm-expression: db$->{user_id % 2 + 1}
          # 分表策略，enterprise_id为偶数操作sjd_user_1，为奇数操作表sjd_user_2
          table-strategy:
          	# 精准分片
            standard:
              # 指定表的分片策略：分片键
              sharding-column: user_code
              # 指定表的分片策略：自定义分片算法
              precise-algorithm-class-name: com.zss.hsl.algorithm.UUIDShardingAlgorithm
```

这个水平分库就相当于同时进行了垂直分库和水平分表，所有不仅仅要配置多数据源，还要配置分库策略`database-stratege`和分表策略`table-stratege`。由于需要分库又分表，就需要两个分片键，一个决定如何分库，一个决定如何分表。如果采用同一个分片键，采用同样的分片算法，那么最后造成的结果是库是分了，但是表没有分（如：Id取模结果为1，根据分库策略，分配到db_1，又根据分表策略，插入数据到table_1，这样的话，db_1.table_2就永远不会有数据，同理，db_2.table_2也不会有数据）。

所有需要两个以上的分片键，决定如何分库分表。在本模块中，对于User表，我使用user_id来决定分库，使用user_code来决定如何分表。但是由于user_code使用的是UUID生成的字符串，所有用groovy表达式就不适合了，这里就需要自定义分片算法。

#### 2. 自定义分片算法

Sharding-JDBC提供了一下四种分片算法的接口

- **PreciseShardingAlgorithm**：精准分片算法
- **RangeShardingAlgorithm**：范围分片算法
- **HintShardingAlgorithm**：Hint分片算法
- **ComplexKeysShardingAlgorithm**：复合键分片算法

本模块分表只有一个分片键，所以使用精准分片算发，使用示例如下：

```java
/**
 * 自定义分片算法 - 获取UUID第一个字符，获取其byte序列，进行取模
 */
public class UUIDShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    /**
     * 在加载配置文件时，会解析表分片规则。将实际表名存储到 Collection中
     * shardingValue: logicTableName=逻辑表明, columnName=字段名称, value=字段值
     *
     * @param collection    实际表名集合
     * @param shardingValue Sql中对应的
     * @return String
     */
    @Override
    public String doSharding(Collection collection, PreciseShardingValue shardingValue) {
        String value = String.valueOf(shardingValue.getValue());
        // 获取UUID的第一个字符对应的byte序列，不管是数值还是字符的byte序列都是数值格式
        byte[] charByte = value.substring(0, 1).getBytes();
        // 去除符号[]
        String temp = Arrays.toString(charByte).replaceAll("[\\[\\]]", "");
        // 取模
        int result = Integer.parseInt(temp) % 2;
        for (Object name : collection) {
            String tableName = String.valueOf(name);
            if (tableName.endsWith(String.valueOf(result + 1))) {
                return tableName;
            }
        }
        return null;
    }
}
```

自定义分片算法编写完成后，只需在配置文件中配置`precise-algorithm-class-name`即可。
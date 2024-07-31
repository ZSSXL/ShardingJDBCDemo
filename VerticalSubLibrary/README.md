## Sharding-JDBC Demo: 垂直分库 - VerticalSubLibrary

**Desc:** Sharding-JDBC学习

**Author:** ZSS

**CreateTime:** 2021-02-23

---

> 本模块是对Sharding-JDBC的垂直分库的实现与测试。具体实现如下：

#### 1. 首先在pom.xml配置需要的maven依赖

```xml
<dependencies>
    <dependency>
        <groupId>com.zss</groupId>
        <artifactId>sharding-demo-api</artifactId>
    </dependency>

    <!-- Sharding-JDBC Start -->
    <dependency>
        <groupId>org.apache.shardingsphere</groupId>
        <artifactId>sharding-jdbc-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.shardingsphere</groupId>
        <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
    </dependency>
    <!-- Sharding-JDBC End -->

    <!-- SpringBoot Start -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- SpringBoot End -->

    <!-- Database Start -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <!-- Database End -->

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
</dependencies>
```

#### 2. 接着是application.yml的配置

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
          # groovy表达式，配置数据节点，db1或db2
          actual-data-nodes: db$->{1..2}.sjd_enterprise
          # 分库策略，enterprise为偶数操作db1，为奇数操作db2
          database-strategy:
            inline:
              sharding-column: enterprise_id
              algorithm-expression: db$->{enterprise_id % 2 + 1}
        # 逻辑表名
        sjd_user:
          # groovy表达式，配置数据节点，db1或者db2
          actual-data-nodes: db$->{1..2}.sjd_user
          # 分库策略
          database-strategy:
            inline:
              # 指定表的分片策略：分片键
              sharding-column: user_id
              # 指定表的分片策略：分片算法
              algorithm-expression: db$->{user_id % 2 + 1}
```

* 本模块因为是测试垂直分库，所有只分库不分表，也就是需要配置两个或两个以上的数据源，各个数据源中创建的表的表名都是逻辑表名。

* 因为只分库不分表，所以只需要配置一个`database-strategy`，不需要配置`table-strategy`

#### 3. 问题和解决

​		这个垂直分库要是实现它本来应该是挺简单的，原理就是在多个数据源中分别创建各个`table`，然后根据一定的策略算法决定到哪个数据源中CRUD即可。但是事情往往并没有这么简单，要不然我也不会在这里卡了一天半。

​		我一开始创建实体插入数据是通过创建序列`SEQUENCE`，然后通过`@SequenceGenerator` ，`@GeneratedValue`这两个注解，实现主键自增。

```sql
CREATE SEQUENCE SEQ_USER_ID
INCREMENT 1
MINVALUE 1000
MAXVALUE 9223372036854775807
START 1000
CACHE 1;
```

```java
@SequenceGenerator(name = "ID_SEQ", sequenceName = "SEQ_ARS_HIBERNATE", allocationSize = 1)
public class User {
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SEQ")
	private Long id;
	
	...
}
```

​    	这样的主键自增方法，在单库中使用是很完美的，但是在多个数据源中使用的话，由于每个数据源中都有创建序列，会造成主键重复而插入数据失败。

​		然后我就决定改用Sharding-JDBC自带的雪花算法`SNOWFLAKE`实现主键自增，只需要在application.yml中在对应的表下进行如下配置：

```yaml
# 配置需要分表操作的表名
tables:
    # 逻辑表名
    sjd_enterprise:
      # groovy表达式，配置数据节点，sjd_enterprise_1/sjd_enterprise_2
      actual-data-nodes: db1.sjd_enterprise_$->{1..2}
      # 分库策略，enterprise为偶数操作db1，为奇数操作db2
          database-strategy:
            inline:
              sharding-column: enterprise_id
              algorithm-expression: db$->{enterprise_id % 2 + 1}
      # 主键自增
      key-generator:
       	 column: enterprise_id
         # 雪花算法(默认)
         type: SNOWFLAKE
```

​		配置好后运行测试，运行又报错了。原因是**主键不能插入NULL值**，就很奇怪，然后我又测试了几遍，隐约觉得这是一个坑，Sharding-JDBC和Hibernate整合的时候，配置的主键自增好像是不管用。

​		然后我就想着，我把这个雪花算法单独通过一个类来实现，用这个类来作为一个策略，添加到实体类的注解中，具体实现如下：

```java
package com.zss.sda.worker;

public class SnowflakeGenerator implements IdentifierGenerator {
	
	...

	/**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long getId() {
    	...
    }

	@Override
    public Serializable generate(SharedSessionContractImplementor sci, Object o) throws HibernateException {
        return getId();
    }
    
    /**
     * 雪花算法测试
     */
    public static void main(String[] args) {
        SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator(0, 0);
        for (int i = 0; i < 10; i++) {
            long id = snowflakeGenerator.getId();
            System.out.println("Id: " + id);
        }
    }
}
```

```java
public class User implements Serializable {
    
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "com.zss.sda.worker.SnowflakeGenerator")
    private Long id;
}
```

​		在SnowflakeGenerator类中测试的时候，生成的是连续的，整数奇数偶数各一半，完美符合预期。然后测试新增数据的时候也没有什么问题，但是多测试了几遍后发现生成的ID是不连续的，而且全部是偶数，这就有点坑了。这个方法看来是不管用。

​		然后我决定通过Redis来实现主键自增，使用Redis的`incr`方法，`key`最好是对应的表名，根据这个想法，看了下`org.hibernate.id.enhanced.SequenceStyleGenerator`的源码，实现如下类：

```java
public class RedisIdGenerator implements IdentifierGenerator, Configurable {
    
    /**
     * 默认表名参数
     */
    public static final String DEFAULT_TABLE_NAME = "public_table";

    /**
     * 参数名
     */
    public static final String TABLE_NAME = "table_name";
    
	private String tableNameValue;
	
	@SuppressWarnings({"WeakerAccess"})
    protected String determineTableNameValue(Properties params) {
        return ConfigurationHelper.getString(Constant.TABLE_NAME, params, Constant.DEFAULT_TABLE_NAME);
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        tableNameValue = determineTableNameValue(params);
        log.info("Get TableName From Parameters: [{}]", tableNameValue);
    }

    private long getIncrId() {
        return RedisUtil.incr(tableNameValue);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {
        return getIncrId();
    }

}
```

```java
public class User implements Serializable {

    /**
     * id
     */
    @Id
    @Column(nullable = false, name = "user_id")
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "com.zss.sda.worker.RedisIdGenerator",
        parameters = @org.hibernate.annotations.Parameter(name = "table_name", value = "sjd_user"))
    private Long id;
}
```

测试之后，达到了自己的预期。只是还有需要完善的地方，就是这样生成的Id都是从1开始递增，我觉得应该添加一个参数，能够灵活的决定从哪个数字开始递增。

​		
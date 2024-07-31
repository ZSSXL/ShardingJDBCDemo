## Sharding-JDBC：水平分表-HorizontalSubTable

**Desc:** Sharding-JDBC学习

**Author:** ZSS

**CreateTime:** 2021-02-22

---

>  本模块是对Sharding-JDBC的水平分表的实现与测试。具体实现如下：

#### 1. 首先是pom.xml配置需要的maven依赖

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
    <!--<dependency>
        <groupId>com.oracle</groupId>
        <artifactId>ojdbc6</artifactId>
    </dependency>-->
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

#### 2. 根据测试需求，设计数据库

```sql
# 创建数据库
CREATE DATABASE SHARDING_DB;
# 创建表
CREATE TABLE SJD_USER (
	user_id	integer not null primary key,
    age		integer,
    user_code varchar(128),
    username  varchar(20),
    enterprise_code  varchar(128),
    `desc`	text
);
CREATE TABLE SJD_ENTERPRISE (
	enterprise_id integer not null primary key,
    enterprise_name	varchar(100),
    enterprise_code varchar(128),
    address	varchar(128),
    email	varchar(50),
    `desc`	text
);
# 创建序列
CREATE SEQUENCE SEQ_ENTERPRISE_ID
INCREMENT 1
MINVALUE 1000
MAXvALUE 9223372036854775807
START 1000
CACHE 1;
CREATE SEQUENCE SEQ_ENTERPRISE_ID
INCREMENT 1
MINVALUE 1000
MAXvALUE 9223372036854775807
START 1000
CACHE 1;
```

以上建库建表都是逻辑上的，实际的库名和表名会因为分库分表策略不同而有所区别。

#### 3. 配置application.yml文件

```yaml
spring:
  # spring-data-jpa配置
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
    # 输出实际SQL日志
    props:
      sql:
        show: true
    datasource:
      # 数据源名称
      names: db1
      # 配置数据源
      db1:
        # 第三方数据库连接池 - druid
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: org.postgresql.Driver
        url: jdbc:postgresql://127.0.0.1:5432/sharding_db_1
        username: sharding
        password: sharding
    sharding:
      # 配置需要分表操作的表名
      tables:
        # 逻辑表名
        sjd_enterprise:
          # groovy表达式，配置数据节点，sjd_enterprise_1/sjd_enterprise_2
          actual-data-nodes: db1.sjd_enterprise_$->{1..2}
          table-strategy:
            inline:
              # 指定表的分片策略：分片键
              sharding-column: enterprise_id
              # 指定表的分片策略：分片算法
              algorithm-expression: sjd_enterprise_$->{enterprise_id % 2 + 1}
        # 逻辑表名
        sjd_user:
          # groovy表达式，配置数据节点，sjd_enterprise_1/sjd_enterprise_2
          actual-data-nodes: db1.sjd_user_$->{1..2}
          table-strategy:
            inline:
              # 指定表的分片策略：分片键
              sharding-column: user_id
              # 指定表的分片策略：分片算法
              algorithm-expression: sjd_user_$->{user_id % 2 + 1}
```

* Sharding-JDBC支持Hibernate，所有可以使用JPA自动建表。以上的配置的意思：根据`actual-data-nodes:`的配置，会分别创建两个表：sjd_user_1，sjd_user_2，sjd_enterprise_1和sjd_enterprise_2；如果配置的是“db1.sjd_enterprise$->{1..3}”，则会分别创建三个表。`algorithm-expression:`这个配置的是分片算法，可以采用groovy表达式，“sjd_enterprise$->{enterprise_id % 2 + 1}”的意思为：会根据enteprise_id的数值对2取模再加1，那么就是如果是双数，那么结果为1，如果是单数，那么结果为2，这样就可以根据这个enterprise_id来决定数据该从哪个表中查询或者是插入到哪个表中，user_id也是如此。


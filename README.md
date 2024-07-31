## Sharding-JDBC Demo

**Desc:** Sharding-JDBC学习

**Author:** ZSS

**CreateTime:** 2021-02-20

---

### 1. Sharding-JDBC介绍

​		`Sharding-JDBC`是当当网研发的开源分布式数据库中间件，从3.0开始Sharding-JDBC被包含在`Sharding-Sphere`中，之后改项目进入Apache孵化器，4.0之后的版本为Apache版本。

​		`Sharding-Sphere`是一套开源的分布式数据库中间件解决方案组成的生态圈，它由`Sharding-JDBC`，`Sharding-Proxy`和`Sharding-Sidecar（计划中）`这三款相互独立的产品组成。它们均提供标准化的数据分片，分布式事务和数据库治理功能，可适用于如Java同构、异构语言、容器、云原生等多种多样的应用场景。

​		`Sharding-JDBC`定位为轻量级Java框架，在Java的JDBC层提供额外的服务。它使用客户端直连数据库，以Jar包形式提供服务，无需额外部署和依赖，可以理解为增强版的JDBC驱动，完全兼容JDBC和各种ORM框架。

​		`Sharding-JDBC`的核心功能为**数据分片**和**读写分离**，通过Sharding-JDBC，应用可以透明的使用JDBC访问已经分库分表、读写分离的多个数据源，而不用关心数据源的数量以及数据如何分布。

* 使用于任何基于Java的ORM框架，如：Hibernate，MyBatis，Spring JDBC Template或直接使用JDBC。
* 基于任何第三方的数据库连接池，如：DBCP，C3P0，BoneCP，Druid，HikariCP等。
* 支持任意实现JDBC规范的数据库。目前支持MySQL，Oracle，SQLServer和PostgreSQL。
* Sharding-JDBC不提供主从数据库的数据同步功能，需要采用其他机制支持。

### 2. 分库分表的方式

​		分库分表包括分库和分表两个部份，在生产中通常包括：垂直分库，水平分库；垂直分表，水平分表；

* **垂直分库**：按照业务将表进行分类，分不到不同的数据库上面，每个库可以放在不同的服务器上，它的核心理念是专库专用。
  * 解决业务层面的耦合，业务清晰；
  * 能对不同业务的数据进行分级管理、维护、监控、扩展等；
  * 高并发场景下，垂直分库一定程度的提升IO、数据库连接数、降低单机硬件资源的瓶颈。

* **水平分库**：把同一个表的数据按一定的策略拆分到不同的数据库这种，每个库可以放在不同的服务器上。
  * 解决了单库大数据，高并发的性能瓶颈；
  * 提高了系统的稳定性及可用性。

* **垂直分表**：将一个表按照字段分成多个表，每个表存储其中一部分字段。
  * 避免IO争抢并减少锁表的几率；
  * 充分发会热门数据的操作效率。

* **水平分表**：在同一个数据库内，把同一个表的数据按照一定的策略规则拆分到多个表中。
  * 优化单一表数据量过大而产生的性能问题；
  * 避免IO争抢并减少锁表的几率。

### 3. 分库分表带来的问题

​		分库分表能有校的缓解单机和单库带来的性能瓶颈和压力，突破网络IO、硬件资源、连接数的瓶颈，同时也带来了一些问题。

* **事务一致性问题**：由于分库分表把数据分布在不同库甚至是不同的服务器，不可避免的会带来`分布式事务`问题。
* **跨节点关联查询**：分库分表后不在同一个数据库，甚至是不在同一台服务器，无法进行关联查询，可以分为多次查询。
* **跨节点分页，排序函数**：跨节点多库进行查询时，limit分页，order by排序等问题，就变得比较复杂了。需要先在不同的分片节点中将数据进行排序并返回，然后将不同分片返回的结果集进行汇总和再次排序。
* **主键避重**：在分库分表环境中，由于表中数据同时存在不同数据库中，主键值平时使用的自增长将无用，某个分区数据库生成的ID无法保证全局唯一。因此需要单独设计全局主键，以避免跨库主键重复问题。
* **公共表**：参数表，数据字典表等都是数据量比较小，变动小，而且属于高频联合查询的依赖表。可以将这类表在每个数据库中都保存一份，所有对公共表的更新操作都同时发送给所有分库进行。

### 4. 开发环境搭建

* **操作系统**：Win10
* **开发工具**：IDEA-2020.2.1
* **数据库**：PostgreSQL-12.3，Oracle-Release.12.2.0.1.0
* **JDK**：jdk.1.8.0_231×64
* **应用框架**：SpringBoot-2.2.8.RELEASE，spring-boot-starter-data-jpa-2.2.8.RELEASE
* **Sharding-JDBC**：sharding-jdbc-core-4.1.0，sharding-jdbc-spring-boot-starter-4.1.0
* 以下是父工程的pom.xml文件部份内容

```xml
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <spring-boot.version>2.2.8.RELEASE</spring-boot.version>
    <sharding.version>4.1.0</sharding.version>
    <druid.version>1.1.21</druid.version>
    <postgresql.version>42.2.18</postgresql.version>
    <oracle.version>12.2.0.1.0</oracle.version>
    <lombok.verson>1.18.10</lombok.verson>
    <commons-lang3.version>3.11</commons-lang3.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- API -->
        <dependency>
            <groupId>com.zss</groupId>
            <artifactId>sharding-demo-api</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Sharding-JDBC Start -->
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-core</artifactId>
            <version>${sharding.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>${sharding.version}</version>
        </dependency>
        <!-- Sharding-JDBC End -->

        <!-- SpringBoot Start -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <!-- SpringBoot End -->

        <!-- Database Start -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>${druid.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgresql.version}</version>
        </dependency>
        <!--<dependency>
            <groupId>com.oracle</groupId>
            <artifactId>ojdbc6</artifactId>
            <version>${oracle.version}</version>
        </dependency>-->
        <!-- Database End -->

        <!-- util start -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.verson}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>${commons-lang3.version}</version>
        </dependency>
        <!-- util end -->
    </dependencies>
</dependencyManagement>
```



### 5. Sharding-JDBC的使用

​		本Demo主要分为一下几个方向进行循序渐进的使用和测试。

* **单库 - 垂直分表**

  这个略吧，这个就没有什么好说的了...

* **单库 - 水平分表**：HorizontalSubTable

* **多库 - 垂直分库**：VerticalSubLibrary

* **多库 - 水平分库**：HorizontalSubLibrary

* **读写分离**：RWS（Read and Write Separation）

### 6. 需要了解的

* 分片策略
  * **standard**：标准分片策略，对应StandardShardingStrategy。提供对SQL语句中的=，IN和BETWEEN AND的分片操作支持。StandardShardingStrategy只支持单分片键，提供PreciseShardingAlgorithm和RangeShardingAlgorithm两个分片算法。PreciseShardingAlgorithm是必选的，用于处理=和IN的分片，RangeShardingAlgorithm是可选的，用于处理BETWEEN AND分片，如果不配置PreciseShardingAlgorithm，SQL中的BETWEEN AND将按照全库路由处理。
  * **complex**：复合分片策略，对应ComplexShardingStrategy。复合分片策略，提供对SQL语句中的=，IN和BETWEEN AND的分片操作支持。ComplexShardingStrategy支持多分片键，由于多分片键之间的关系复杂，因此并未进行过多的封装，而是直接将分片键值组合以及分片操作符透传至分片算法，完全由应用开发者实现，提供最大的灵活度。
  * **inline**：行表达式分片策略，对应InlineShardingStrategy。使用Groovy的表达式，提供对SQL语句中的=和IN的分片操作支持，只支持单分片键。对于简单的分片算法，可以通过简单的配置使用，从而避免繁琐的Java代码开发。如：`t_user_$->(u_id % 2 + 1)`表示t_user表根据u_id模2，从而分成两张表，标名为`t_user_1`和`t_user_2`。
  * **hint**：Hint分片策略，对应HintShardingStrategy。通过Hint而非SQL解析的方式分片的策略。对于分片字段非SQL决定，而由其他外置条件决定的场景，可使用SQL Hint灵活的注入分片字段。例：内部系统，按照员工登录主键分库，而数据库中并无此字段。SQL Hint支持通过Java API和SQL注释两种方式实现。
  * **none**：不分片策略，对应NoneShardingStrategy。不分片的策略。
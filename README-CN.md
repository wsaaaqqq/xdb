[[中文简介]](https://github.com/wsaaaqqq/xdb/blob/main/README-CN.md) [[英文简介]](https://github.com/wsaaaqqq/xdb/blob/main/README.md)

# xdb framework for jdbc
xdb是一种jdbc工具包，可以方便的进行jdbc操作,尤其是复杂的查询操作，并且很容易的与springboot进行集成。
## 示例
~~~
## pom.xml
------------------------------
## in springboot project
<dependency>
    <groupId>io.github.wsaaaqqq</groupId>
    <artifactId>xdb-springboot-starter</artifactId>
    <version>1.0.5-RELEASE</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.5.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
~~~

~~~
#### 配置数据源
#### application.properties
-------------------------------------------------------------------------------------------------------
spring.datasource.url=jdbc:h2:mem:testH2db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.data.jdbc.repositories.enabled=false
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
~~~

~~~
#### 准备一个允许多个查询参数的sql文件
#### files/sql/user_query.sql
-------------------------------------------------------------------------------------------------------
select id,name from t_user
where 1=1
-- and info LIKE '%'|| :info ||'%'
-- and id in (:ids)
~~~

~~~
#### 根据实际的入参进行，开启对应参数的查询条件
#### xht.xdb.demo.controller.TestController
-------------------------------------------------------------------------------------------------------
@RequestMapping("/user/")
public List<Map<String, Object>> query(
        @RequestParam(required = false) String info,
        @RequestParam(required = false) String[] ids,
        @RequestParam(required = false) Long p,
        @RequestParam(required = false) Long s
) {
    MapUtil sqlArgs = MapUtil.init()
            .addOnlyNotNull("info", info)
            .addOnlyNotNull("ids", ids);
    return Xdb.sqlFile("user/user_query.sql")
            .sqlArgs(sqlArgs)
            .pageIndex(p)
            .pagePerSize(s)
            .executeQuery()
            .result();
}
~~~

更多使用方法,请参阅：
[[xdb-springboot-starter-example]](https://github.com/wsaaaqqq/xdb/tree/main/xdb-springboot-starter-example)
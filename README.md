[[中文简介]](https://github.com/wsaaaqqq/xdb/blob/main/README-CN.md) [[英文简介]](https://github.com/wsaaaqqq/xdb/blob/main/README.md)

# xdb framework for jdbc
XdB is a JDBC toolkit, which can facilitate JDBC operations, especially complex query operations, and is easy to integrate with spring boot.

## usage

example project： 
[[xdb-springboot-starter-example]](https://github.com/wsaaaqqq/xdb/tree/main/xdb-springboot-starter-example)

> The main steps are as follows: 
 
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
#### config datasource
#### application.properties
-------------------------------------------------------------------------------------------------------
spring.datasource.url=jdbc:h2:mem:testH2db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.data.jdbc.repositories.enabled=false
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
~~~



~~~
#### Prepare an SQL file that allows multiple query parameters
#### files/sql/user_query.sql
------------------------------
select id,name from t_user
where 1=1
-- and info LIKE '%'|| :info ||'%'
-- and id in (:ids)
~~~

~~~
#### According to the actual input parameters, open the query criteria of the corresponding parameters
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

For more usage, please refer to： 
[[wiki]](https://github.com/wsaaaqqq/xdb/wiki)

[[中文简介]](https://github.com/wsaaaqqq/xdb/blob/main/README-CN.md) [[英文简介]](https://github.com/wsaaaqqq/xdb/blob/main/README.md)

# xdb framework for jdbc
The xdb framework makes it easier to use a relational database. It is very easy to integrate with spring boot.

## usage

~~~
Xdb.sql("select id,name from user where name like '%'||:name||'%' and id=:id")
    .sqlArg("name","xht")
    .sqlArg("id","1")
    .pageIndex(1)
    .pagePerSize(10)
    .executeQuery()
    .result();
~~~
or 
~~~
#### Prepare an SQL file that allows multiple query parameters
#### files/sql/user_query.sql
------------------------------
select id,name from user 
where 1=1
-- and name like '%'||:name||'%' 
-- and id=:id
~~~
~~~
#### According to the actual input parameters, open the query criteria of the corresponding parameters
Xdb.sqlFile("files/sql/user_query.sql")
    .sqlArg("name","xht") //only “and name like...” will take effect 
    .pageIndex(1)
    .pagePerSize(10)
    .executeQuery()
    .result();
~~~

For more usage, please refer to： 
[[xdb-springboot-starter-example]](https://github.com/wsaaaqqq/xdb/tree/main/xdb-springboot-starter-example)
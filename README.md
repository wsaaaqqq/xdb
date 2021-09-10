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
#### files/sql/user_query1.sql
------------------------------
select id,name from user 
where 1=1
-- and name like '%'||:name||'%' 
-- and id=:id
~~~
~~~
Xdb.sqlFile("files/sql/user_query1.sql")
    .sqlArg("name","xht") //only “and name like...” will take effect 
    .pageIndex(1)
    .pagePerSize(10)
    .executeQuery()
    .result();
~~~
For more usage, please refer to： _**xdb-springboot-starter-example**_
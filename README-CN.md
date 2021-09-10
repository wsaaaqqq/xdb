# xdb framework for jdbc
xdb是一种jdbc工具包，可以方便的进行jdbc操作，并且很容易的与springboot进行集成。
## 示例

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
    .sqlArg("name","xht") //只有 “and name like...” 过滤条件会生效 
    .pageIndex(1)
    .pagePerSize(10)
    .executeQuery()
    .result();
~~~
更多使用方法,请参阅： _**xdb-springboot-starter-example**_
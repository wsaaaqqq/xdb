select id,name from t_user
where 1=1
-- and info LIKE '%'|| :info ||'%'
-- and id in (:ids)
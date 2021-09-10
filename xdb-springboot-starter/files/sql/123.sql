select code,code_full from core
where 1=1
-- and code in (:code)
-- and code_full like '%'||:code_full||'%'
package xht.xdb.demo.controller;

import org.springframework.web.bind.annotation.RequestParam;
import xht.xdb.Xdb;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xht.xdb.demo.jpa.User;
import xht.xdb.demo.jpa.UserRepository;
import xht.xdb.util.MapUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class TestController {
    private final UserRepository tTestRepository;

    @RequestMapping("/")
    public List<Map<String, Object>> all(){
        return Xdb.sql("select id,name,info from t_user")
                .executeQuery()
                .result();
    }

    @RequestMapping("/q")
    public List<Map<String, Object>> query(
            @RequestParam(required = false) String info,
            @RequestParam(required = false) String[] ids
    ){
        MapUtil sqlArgs = MapUtil.init()
                .addOnlyNotNull("info",info)
                .addOnlyNotNull("ids",ids)
                ;
        return Xdb.sqlFile("user/user_query.sql")
                .sqlArgs(sqlArgs)
                .pageIndex(1)
                .pagePerSize(10)
                .executeQuery()
                .result();
    }

    @RequestMapping("/s")
    public User save(){
        String uuid = UUID.randomUUID().toString();
        return tTestRepository.save(new User()
                .setId(uuid)
                .setName("name".concat(uuid))
                .setInfo("info".concat(uuid))
        );
    }

    @RequestMapping("/r")
    public int remove(){
        return Xdb.sql("delete from t_user").executeUpdate().result();
    }

}

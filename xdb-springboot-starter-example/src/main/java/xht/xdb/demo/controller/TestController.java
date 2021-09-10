package xht.xdb.demo.controller;

import xht.xdb.Xdb;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xht.xdb.demo.jpa.User;
import xht.xdb.demo.jpa.UserRepository;

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
    public List<Map<String, Object>> query(){
        return Xdb.sql("select id,name from t_user where info like '%'||:info||'%'")
                .sqlArg("info","1")
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

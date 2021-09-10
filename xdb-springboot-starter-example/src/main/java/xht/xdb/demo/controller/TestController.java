package xht.xdb.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xht.xdb.Xdb;
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

    @RequestMapping("/user/s")
    public User save() {
        String uuid = UUID.randomUUID().toString();
        return tTestRepository.save(new User()
                .setId(uuid)
                .setName("name".concat(uuid))
                .setInfo("info".concat(uuid))
        );
    }

    @RequestMapping("/user/d")
    public int delete() {
        return Xdb.sql("delete from t_user").executeUpdate().result();
    }

}

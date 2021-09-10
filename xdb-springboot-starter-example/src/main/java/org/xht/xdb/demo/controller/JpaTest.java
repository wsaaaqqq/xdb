package org.xht.xdb.demo.controller;

import xht.xdb.Xdb;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xht.xdb.demo.jpa.User;
import org.xht.xdb.demo.jpa.UserRepository;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class JpaTest {
    private final UserRepository tTestRepository;

    @RequestMapping("/")
    public Object query(){
        return Xdb.sql("select * from T_TEST")
                .executeQuery()
                .result();
    }

    @RequestMapping("/s")
    public Object save(){
        return tTestRepository.save(new User()
                .setMC("1")
                .setID(UUID.randomUUID().toString())
        );
    }

    @RequestMapping("/r")
    public Object remove(){
        return Xdb.sql("truncate table T_TEST").executeUpdate();
    }

}

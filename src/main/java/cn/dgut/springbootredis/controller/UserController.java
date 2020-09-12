package cn.dgut.springbootredis.controller;

import cn.dgut.springbootredis.annotation.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuZhulan
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @GetMapping("/list")
    @Cache(expireTime = 10)
    public String list(){
        log.info("操作数据库");
        return "user list";
    }
}

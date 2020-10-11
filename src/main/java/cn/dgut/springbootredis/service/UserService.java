package cn.dgut.springbootredis.service;

import cn.dgut.springbootredis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LiuZhulan
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private RedisService redisService;

    public User  getUserById(Integer id){

        Object o = redisService.get(String.valueOf(id), User.class);
        if(o!=null){
            return (User)o;
        }
        log.info("缓存不存在查询数据库");
        User user=new User(id,"Liu");
        redisService.set(String.valueOf(id),user,1000);
        return user;
    }
}

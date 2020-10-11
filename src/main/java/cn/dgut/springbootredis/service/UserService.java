package cn.dgut.springbootredis.service;

import cn.dgut.springbootredis.entity.User;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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

        Object o = redisService.get(String.valueOf(id));
        if(o!=null){
            return (User)o;
        }
        User user=new User(id,"Liu");
        log.info("缓存不存在查询数据库");
        redisService.set(String.valueOf(id),user,1000);
        return user;
    }

    /**
     * 缓存空对象的方式防止缓存穿透
     * 缺点：每次访问一个不存在的数据，都会去查一次数据库；大量空数据存储在Redis中
     * @param id
     * @return
     */
    public User  getUserByIdWirhNull(Integer id){


        String s = JSON.parse(redisService.get(String.valueOf(id))).toString();
        User user;
        //JSON.parse解析去掉\
        if(s==null||s.equals("{}")){
            log.info("返回空对象");
            return null;
        }
        user= JSON.parseObject(s.toString(),User.class);

        if(user==null){
            User n=new User(id,"Liu");
            n=id==1?n:null;
            if(n==null){
                //数据库没有这条数据，缓存空对象
                log.info("数据库不存在id为{}的数据，缓存空对象",id);
                redisService.set(String.valueOf(id), JSON.toJSONString(new User()),1000);
            }else{
                log.info("缓存不存在查询数据库");
                redisService.set(String.valueOf(id), JSON.toJSONString(n),1000);
            }
            return n;
        }else {
            log.info("缓存中取");
           return user;
        }
    }
}

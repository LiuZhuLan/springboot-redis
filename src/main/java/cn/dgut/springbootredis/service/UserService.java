package cn.dgut.springbootredis.service;

import cn.dgut.springbootredis.entity.NullValue;
import cn.dgut.springbootredis.entity.User;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author LiuZhulan
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private RedisService redisService;

    @Resource(name = "redisTemplate")
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ValueOperations<String, String> operations;

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

        Object o = operations.get(String.valueOf(id));
        if(o!=null){
            if(o instanceof NullValue){
                //返回提示
                log.info("空对象");
                return null;
            }
            return (User)o;
        }

        User user=new User(id,"Liu");
        user=id==1?user:null;
        if(user!=null){
            log.info("缓存不存在查询数据库");
            operations.set(String.valueOf(id), JSON.toJSONString(user),1000);
        }else {
            //数据库没有这条数据，缓存空对象
            log.info("数据库不存在id为{}的数据，缓存空对象",id);
            operations.set(String.valueOf(id), JSON.toJSONString(new NullValue()),1000);
        }

        return user;
    }
}

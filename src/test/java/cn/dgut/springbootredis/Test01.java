package cn.dgut.springbootredis;

import cn.dgut.springbootredis.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author LiuZhulan
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test01 {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;

    /**
     * 测试自定义缓存注解
     * @return
     */
    @Test
    public void test1(){
        stringRedisTemplate.opsForValue().set("liu","lan");
        System.out.println(stringRedisTemplate.opsForValue().get("liu"));
    }

   @Test
    public void test2(){
       userService.getUserById(1);
    }

    /**
     * 缓存空对象
     */
    @Test
    public void test3(){
        userService.getUserByIdWirhNull(2);
    }
}

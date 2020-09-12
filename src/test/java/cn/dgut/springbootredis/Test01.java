package cn.dgut.springbootredis;

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

    @Test
    public void test1(){
        stringRedisTemplate.opsForValue().set("liu","lan");
        System.out.println(stringRedisTemplate.opsForValue().get("liu"));
    }
}

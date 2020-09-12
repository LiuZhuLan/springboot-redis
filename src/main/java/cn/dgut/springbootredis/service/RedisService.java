package cn.dgut.springbootredis.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author LiuZhulan
 */
@Service
@Slf4j
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public <T> void set(String key, T value, int expireTime) {

        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        stringStringValueOperations.set(key, JSON.toJSONString(value),expireTime, TimeUnit.SECONDS);
    }
    public Object get(String key,Class clazz){

        String value=redisTemplate.opsForValue().get(key);
        return JSON.parseObject(value,clazz);
    }
}

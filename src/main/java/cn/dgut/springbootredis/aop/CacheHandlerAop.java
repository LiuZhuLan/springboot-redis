package cn.dgut.springbootredis.aop;

import cn.dgut.springbootredis.annotation.Cache;
import cn.dgut.springbootredis.service.RedisService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.serializer.SerializerFeature;


import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author LiuZhulan
 */
@Aspect
@Component
@Slf4j
public class CacheHandlerAop {

    private static final SerializerFeature[] FEATURES;

    static {
        FEATURES = new SerializerFeature[]{SerializerFeature.MapSortField, SerializerFeature.SortField};
    }

    @Autowired
    private RedisService redisService;

    @Around("@annotation(cn.dgut.springbootredis.annotation.Cache)")
    public Object cacheAop(ProceedingJoinPoint joinPoint) throws Throwable {

        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature)signature;
            Cache cache = (Cache)methodSignature.getMethod().getAnnotation(Cache.class);
            if (cache!=null) {
                String key = this.generateCacheKey(cache, joinPoint, methodSignature);
                return this.handlerCache(key, cache, joinPoint);
            }
        }
        return joinPoint.proceed();

    }

    protected String generateCacheKey(Cache cache, ProceedingJoinPoint invocation, MethodSignature methodSignature)throws JSONException {

        String cacheKey=cache.key();

        //设置注解的时候没有写key
        if (StrUtil.isBlank(cacheKey)) {
            Object[] args = invocation.getArgs();
            cacheKey = String.format(methodSignature.toLongString() + ".%s", JSON.toJSONString(args, FEATURES));
        }

        return SecureUtil.md5(cacheKey);
    }


    /**
     * Redis有值则返回，无则设置
     * @param key
     * @param cache
     * @param invocation
     * @return
     * @throws Throwable
     */
    protected Object handlerCache(String key, Cache cache, ProceedingJoinPoint invocation) throws Throwable {

        Object object = redisService.get(key,Object.class);
        if(object!=null){
            log.info("缓存获取");
        }
        //双重检查,高并发情况下缓存失效产生缓存穿透问题
        if (Objects.isNull(object)) {
            synchronized(SecureUtil.md5(key).intern()) {
                object = redisService.get(key,Object.class);
                if(object!=null){
                    log.info("缓存获取");
                }
                if (Objects.isNull(object)) {
                    object = invocation.proceed();
                    if (object instanceof Serializable) {
                        redisService.set(key, object, cache.expireTime());
                    }
                }
            }
        }

        return object;

    }

}

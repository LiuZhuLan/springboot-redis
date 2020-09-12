package cn.dgut.springbootredis.annotation;

import java.lang.annotation.*;

/**
 * @author LiuZhulan
 * @Description
 * @date 2020/09/11/0011 20:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Cache {

    String key() default "";

    int expireTime() default 3;
}

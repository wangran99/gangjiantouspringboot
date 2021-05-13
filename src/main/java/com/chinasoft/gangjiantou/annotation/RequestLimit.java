package com.chinasoft.gangjiantou.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {
    //方法名称
    String methodName() default "";

    //访问次数
    int maxCount() default 10;

    //业务KEY
    String paramKey() default "CDPathSta";

    //请求地址
    String url() default "";

    //过期时间(单位秒)
    long timeout() default 60;
}

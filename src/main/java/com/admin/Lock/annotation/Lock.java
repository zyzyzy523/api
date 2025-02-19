package com.admin.Lock.annotation;



import com.admin.Lock.LockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 分布式锁注解
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/01/11
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Lock {
    /**
     * 锁的名称 为空时则取方法名称
     */
    String name() default "";

    /**
     * 锁类型
     */
    LockType lockType() default LockType.TRY_LOCK;

    /**
     * 尝试加锁，最多等待时间
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 上锁以后xxx毫秒自动解锁
     */
    long leaseTime() default Long.MIN_VALUE;

    /**
     * 自定义业务key 单个对象使用 支持spel表达式
     */
    String[] keys() default {};

    /**
     * 自定义业务key 集合对象使用 支持spel表达式 需要返回List
     */
    String listKey() default "";
}

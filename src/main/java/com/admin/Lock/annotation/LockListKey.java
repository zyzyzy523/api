package com.admin.Lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>
 * 参数注解，获取集合List
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/01/11
 */
@Target(value = {ElementType.PARAMETER, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface LockListKey {
    String value() default "";
}

package com.admin.common.util;


import org.springframework.context.ApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
@Component
public class SpringContextUtils {
    private static ApplicationContext applicationContext;

    public SpringContextUtils(ApplicationContext applicationContext) {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static String getProperty(String name) {
        return ((StandardEnvironment)getApplicationContext().getBean(StandardEnvironment.class)).getProperty(name);
    }

    public static ApplicationContext getContext() {
        return getApplicationContext();
    }

    public static <T> T getBean(Class<T> clazz) {
        return (T)getApplicationContext().getBean(clazz);
    }

    public static <T> void getBeanThen(Class<T> clazz, Consumer<T> consumer) {
        if (applicationContext.getBeanNamesForType(clazz, false, false).length > 0) {
            consumer.accept(applicationContext.getBean(clazz));
        }

    }

    public static <T> T getBean(String name) {
        return (T)getApplicationContext().getBean(name);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return getContext().getBeansOfType(clazz);
    }

    public <T> void getBeansThen(Class<T> clazz, Consumer<List<T>> consumer) {
        if (applicationContext.getBeanNamesForType(clazz, false, false).length > 0) {
            Map<String, T> beansOfType = applicationContext.getBeansOfType(clazz);
            List<T> clazzList = new ArrayList();
            beansOfType.forEach((k, v) -> clazzList.add(v));
            consumer.accept(clazzList);
        }

    }
}

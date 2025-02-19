package com.admin.Lock.config;


import com.admin.Lock.core.LockAspectHandler;
import com.admin.Lock.core.LockInfoProvider;
import com.admin.Lock.core.LockKeyProvider;
import com.admin.Lock.core.LockProvider;
import com.admin.Lock.core.LockProviderImpl;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * Bean
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/01/11
 */
@Configuration
@EnableConfigurationProperties(LockProperties.class)
@Import({LockAspectHandler.class})
public class LockConfig {
    @Autowired
    private RedissonClient redisson;
    @Autowired
    private LockProperties lockProperties;

    @Bean
    public LockProvider lockProvider() {
        return new LockProviderImpl(redisson, lockProperties.getSleeptime());
    }

    @Bean
    public LockInfoProvider lockInfoProvider() {
        return new LockInfoProvider();
    }

    @Bean
    public LockKeyProvider lockKeyProvider() {
        return new LockKeyProvider();
    }


}

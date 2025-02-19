package com.admin.auth.service;


import com.admin.common.constant.CacheConstants;
import com.admin.common.util.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;


@CacheConfig(cacheNames = {CacheConstants.TOKEN_CLIENT}, cacheManager = "authCacheManager")
@Component
public class BaseClientDetailService extends JdbcRegisteredClientRepository {

    @Autowired
    private RedisHelper redisHelper;

    public BaseClientDetailService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }


    @Override
    public void save(RegisteredClient registeredClient) {
        super.save(registeredClient);
        redisHelper.deleteByKey(CacheConstants.TOKEN_CLIENT.concat("::").concat(registeredClient.getClientId()));
    }
    @Cacheable(key = "#clientId", unless = "#result == null")
    public RegisteredClient findByClientId(String clientId) {
        return super.findByClientId(clientId);
    }


}

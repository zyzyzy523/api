package com.admin.auth.service;


import cn.hutool.core.util.StrUtil;
import com.admin.auth.json.PrincipalLiteMixin;
import com.admin.auth.user.PrincipalLite;
import com.admin.common.util.RedisHelper;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import com.admin.common.constant.CacheConstants;
/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/9/8
 */

@CacheConfig(cacheNames = {CacheConstants.TOKEN_VALUE}, cacheManager = "authCacheManager")
public class BaseAuthorizationService extends JdbcOAuth2AuthorizationService {

    private RedisHelper redisHelper;

    public BaseAuthorizationService(JdbcOperations jdbcOperations,
                                    RegisteredClientRepository registeredClientRepository,
                                    RedisHelper redisHelper) {
        this(jdbcOperations, registeredClientRepository, new DefaultLobHandler());
        this.redisHelper = redisHelper;
    }

    public BaseAuthorizationService(JdbcOperations jdbcOperations,
                                    RegisteredClientRepository registeredClientRepository,
                                    LobHandler lobHandler) {
        super(jdbcOperations, registeredClientRepository, lobHandler);
        RowMapper<OAuth2Authorization> authorizationRowMapper = getAuthorizationRowMapper();
        if (authorizationRowMapper instanceof OAuth2AuthorizationRowMapper mapper) {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
            List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
            objectMapper.registerModules(securityModules);
            objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
            objectMapper.addMixIn(PrincipalLite.class, PrincipalLiteMixin.class);
            mapper.setObjectMapper(objectMapper);
        }
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        super.save(authorization);
        if (authorization.getAccessToken() != null && authorization.getAccessToken().getToken() != null) {
            redisHelper.deleteByKey(CacheConstants.TOKEN_VALUE.concat("::").concat(authorization.getAccessToken().getToken().getTokenValue()));
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        super.remove(authorization);
        if (authorization.getAccessToken() != null) {
            redisHelper.deleteByKey(CacheConstants.TOKEN_VALUE.concat("::").concat(authorization.getAccessToken().getToken().getTokenValue()));
        }
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return super.findById(id);
    }

    @Override
    @Cacheable(key = "#token", unless = "#result == null or 'refresh_token'.equals(#tokenType.value)")
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        OAuth2Authorization result = super.findByToken(token, tokenType);
        if (result != null && OAuth2TokenType.REFRESH_TOKEN.getValue().equals(tokenType.getValue())) {
            // 刷新token 删除老的token缓存
            redisHelper.deleteByKey(CacheConstants.TOKEN_VALUE.concat("::")
                    .concat(result.getAccessToken().getToken().getTokenValue()));
        }
        if (result == null && OAuth2TokenType.REFRESH_TOKEN.getValue().equals(tokenType.getValue())) {
            // 找不到刷新token
            throw new OAuth2AuthenticationException(new OAuth2Error("ss"),"",
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "会话已过期"));
        }
        if (result != null && "code".equals(tokenType.getValue())) {
            // 授权码
            OAuth2AuthorizationRequest authorizationRequest = result.getAttribute(
                    OAuth2AuthorizationRequest.class.getName());
            if (authorizationRequest != null && StrUtil.isNotBlank(authorizationRequest.getRedirectUri()) && authorizationRequest.getRedirectUri().endsWith("/")) {
                OAuth2AuthorizationRequest build = OAuth2AuthorizationRequest
                        .from(authorizationRequest)
                        .redirectUri(authorizationRequest.getRedirectUri().substring(0, authorizationRequest.getRedirectUri().length() - 1))
                        .build();
                return OAuth2Authorization
                        .from(result
                        ).attribute(OAuth2AuthorizationRequest.class.getName(), build)
                        .build();

            }
        }
        return result;
    }
}

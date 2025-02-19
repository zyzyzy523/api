package com.admin.auth.service;

import cn.hutool.core.bean.BeanUtil;

import com.admin.auth.user.PrincipalLite;
import com.admin.common.dto.CustomUser;
import com.admin.filter.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/9/8
 */
@Slf4j
public class CheckTokenService implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    public CheckTokenService(OAuth2AuthorizationService oAuth2AuthorizationService) {
        this.oAuth2AuthorizationService = oAuth2AuthorizationService;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2Authorization oAuth2Authorization = getOauth2Authorization(token);
        Map<String, Object> attributes = oAuth2Authorization.getAttributes();
        Object o = attributes.get("java.security.Principal");
        if (o instanceof UsernamePasswordAuthenticationToken pr) {
            Object principal = pr.getPrincipal();
            if (principal instanceof PrincipalLite u) {
                OAuth2AuthenticatedPrincipal user = new OAuth2AuthenticatedPrincipal() {
                    @Override
                    public String getName() {
                        return u.getUsername();
                    }

                    @Override
                    public Map<String, Object> getAttributes() {
                        return BeanUtil.beanToMap(u);
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return u.getAuthorities();
                    }
                };
                UserContext.set(u);
                return user;
            }
        }

        CustomUser customUserDetail = new CustomUser();
        customUserDetail.setClientId(oAuth2Authorization.getPrincipalName());
        UserContext.set(customUserDetail);
        return new OAuth2AuthenticatedPrincipal() {
            @Override
            public String getName() {
                return oAuth2Authorization.getPrincipalName();
            }

            @Override
            public Map<String, Object> getAttributes() {
                return Map.of("clientId", oAuth2Authorization.getPrincipalName());
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptyList();
            }
        };
    }

    public OAuth2Authorization getOauth2Authorization(String token) {
        OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (oAuth2Authorization == null) {
            // 返回403 没权限
            log.warn("token [{}] not found！", token);
            throw new OAuth2AuthenticationException(new OAuth2Error("ss"),"",
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "会话已过期"));
        }
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = oAuth2Authorization.getAccessToken();
        boolean expired = accessToken.isExpired();
        if (expired) {
            // token 过期了
            throw new OAuth2AuthenticationException(new OAuth2Error("ss"),"",
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "会话已过期"));
        }
        return oAuth2Authorization;
    }
}

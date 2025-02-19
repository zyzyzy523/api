package com.admin.auth.service;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;



/**
 * <p>
 * 退出登录处理器
 * </p>
 *
 * @author bin.xie
 * @since 2019/6/5
 */
@Component
@Slf4j
public class LogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
        implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    private static final String BEARER_AUTHENTICATION = "Bearer ";
    private static final String USER_AGENT = "user-agent";

    @Autowired
    private BaseAuthorizationService authorizationService;

    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                String token) {

    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        String token = request.getHeader("authorization");
        if (token != null && token.startsWith(BEARER_AUTHENTICATION)) {
            onLogoutSuccess(request, response, StringUtils.substringAfter(token, BEARER_AUTHENTICATION));
            return;
        }
        String accessToken = request.getParameter("access_token");
        if (StringUtils.isNotEmpty(accessToken)) {
            onLogoutSuccess(request, response, accessToken);
        }
    }
}

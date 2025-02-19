package com.admin.auth.authentication.password;


import cn.hutool.core.collection.CollUtil;
import com.admin.auth.authentication.Oauth2Util;
import com.admin.common.constant.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/9/6
 */
public class PasswordGrantAuthenticationConverter implements AuthenticationConverter {

    private final Set<String> PARAMETER_NAMES = Stream.of(
            OAuth2ParameterNames.USERNAME,
            OAuth2ParameterNames.PASSWORD,
            Constants.LOGIN_TYPE,
            OAuth2ParameterNames.SCOPE).collect(Collectors.toSet());
    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {
        // grant_type (REQUIRED)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!"password".equals(grantType)) {
            return null;
        }

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

        // 从request中提取请求参数，然后存入MultiValueMap<String, String>
        MultiValueMap<String, String> parameters = Oauth2Util.getParameters(request);

        // username (REQUIRED)
        String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
        if (!StringUtils.hasText(username) ||
                parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
            throw new OAuth2AuthenticationException("无效请求，用户名不能为空！");
        }
        String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
        if (!StringUtils.hasText(password) ||
                parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
            throw new OAuth2AuthenticationException("无效请求，密码不能为空！");
        }

        // 收集要传入PasswordGrantAuthenticationToken构造方法的参数，
        // 该参数接下来在PasswordGrantAuthenticationProvider中使用
        Map<String, Object> additionalParameters = new HashMap<>();
        //遍历从request中提取的参数，排除掉grant_type、client_id、code等字段参数，其他参数收集到additionalParameters中
        PARAMETER_NAMES.forEach(v -> {
            if (CollUtil.isNotEmpty(parameters.get(v))) {
                additionalParameters.put(v, parameters.get(v).get(0));
            }
        });
        //返回自定义的PasswordGrantAuthenticationToken对象
        return new PasswordGrantAuthenticationToken(clientPrincipal, additionalParameters);
    }



}



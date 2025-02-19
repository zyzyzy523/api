package com.admin.auth.authentication.authorization;

import cn.hutool.core.net.URLEncoder;

import com.admin.common.constant.Constants;
import com.admin.config.AppProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/4/28
 */
public class CuxAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(CuxAuthenticationFailureHandler.class);
    private final AppProperties appProperties;

    public CuxAuthenticationFailureHandler(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private static Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> parameters = new HashMap<>(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            for (String value : values) {
                parameters.put(key, value);
            }
        });
        return parameters;
    }
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        Map<String, String> parameters = getParameters(request);
        parameters.remove(Constants.TOKEN);
        // 登录的url
        String appUrl = appProperties.getAppUrl();
        String loginUrl = appUrl + "/#/user/login";
        // 授权码的url
        StringBuilder authorizeUrl = new StringBuilder(appUrl + "/oauth2/authorize");
        Set<Map.Entry<String, String>> entries = parameters.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            String parameterName = entry.getKey();
            if (Constants.TOKEN.equalsIgnoreCase(parameterName)) {
                continue;
            }
            if (i == 0) {
                authorizeUrl.append("?");
            } else {
                authorizeUrl.append("&");
            }
            authorizeUrl.append(entry.getKey()).append("=").append(entry.getValue());
            i++;
        }
        authorizeUrl.append(i > 0 ? "&" : "?")
                .append(Constants.TOKEN)
                .append("=").append("{" + Constants.TOKEN + "}");
        loginUrl = loginUrl + "?redirect_path=" +
                URLEncoder.createAll().encode(authorizeUrl.toString(), StandardCharsets.UTF_8);
        this.redirectStrategy.sendRedirect(request, response, loginUrl);
    }
}

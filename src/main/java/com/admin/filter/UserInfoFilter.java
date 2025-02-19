package com.admin.filter;

import cn.hutool.core.util.StrUtil;
import com.admin.common.util.SpringContextUtils;
import com.admin.exception.ExceptionDetail;
import com.admin.utils.HttpResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/9/21
 */
@Component
@Slf4j
public class UserInfoFilter extends GenericFilterBean {

    @Value("${spring.application.name:}")
    private String appName;
    @Value("${fabric.base.app-url:https://dev.byteplan.com}")
    private String url;

    private final RestTemplate restTemplate = new RestTemplate();

    AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/api/**");

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);

    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            RequestMatcher.MatchResult matcher = requestMatcher.matcher(request);
            if (matcher.isMatch()) {
                // 看看有没有token
                String authorization = request.getHeader("Authorization");
                if (StrUtil.isBlank(authorization)) {
                    // 看看参数有没有 access_token
                    String accessToken = request.getParameter("access_token");
                    if (StrUtil.isBlank(accessToken)) {
                        ExceptionDetail detail = new ExceptionDetail();

                        detail.setPath(request.getPathInfo());
                        detail.setError("您没有访问该资源的权限");
                        detail.setMessage("您没有访问该资源的权限");
                        detail.setException("您没有访问该资源的权限");
                        detail.setCategory("ERROR");
                        HttpResponseUtil.out(response, 401, detail);
                        return;
                    }
                    SpringContextUtils
                            .getBean(OpaqueTokenIntrospector.class)
                            .introspect(accessToken);
                }
            }
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }


}

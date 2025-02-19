package com.admin.filter;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.utils.HttpResponseUtil;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import java.io.IOException;
import java.util.Optional;

/**
 * <p>
 * 滑动验证码校验
 * </p>
 *
 * @author bin.xie
 * @since 2021/11/5
 */
@Slf4j
public class ValidateCodeFilter implements Filter {
    private final CaptchaService captchaService;
    private final ServerProperties serverProperties;
    private final String[] tokenUrls = new String[]{"/login", "/oauth2/token"};
    private final String[] OtherValidateUrls = new String[]{"/util/send/verification/code"};

    public ValidateCodeFilter(CaptchaService captchaService,
                              ServerProperties serverProperties) {
        this.captchaService = captchaService;
        this.serverProperties = serverProperties;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);

    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String contextPath = Optional
                .ofNullable(serverProperties.getServlet())
                .map(ServerProperties.Servlet::getContextPath)
                .orElse(null);
        String uri = request.getRequestURI();
        if (StrUtil.isNotBlank(contextPath)) {
            uri = StrUtil.split(uri, contextPath, 2, false, false).get(1);
        }
        boolean isTokenUrl = StrUtil.equalsAnyIgnoreCase(uri, tokenUrls);
        if (!isTokenUrl && !StrUtil.equalsAnyIgnoreCase(uri, OtherValidateUrls)) {
            // 不是需要验证码的url
            filterChain.doFilter(request, response);
            return;
        }
        if (isTokenUrl) {
            // 是获取token的url 只对密码模式进行验证码校验
            String grantType = request.getParameter("grant_type");
            if (!"password".equals(grantType)) {
                // 不是密码模式  比如授权码获取token 还有刷新token 客户端模式
                filterChain.doFilter(request, response);
                return;
            }

        }
        String code = request.getParameter("slideCode");
        if (StrUtil.isBlank(code)) {
            HttpResponseUtil.out(response, 200, MapUtil.of("code", 416));
            return;
        }
        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaVerification(code);
        if (!captchaService.verification(vo).isSuccess()) {
            log.error("code is [{}]", code);
            HttpResponseUtil.out(response, 200, MapUtil.of("code", 500));
            return;
        }
        filterChain.doFilter(request, response);
    }


}
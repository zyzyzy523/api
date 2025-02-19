package com.admin.auth.service;


import com.admin.common.util.JsonUtil;
import com.admin.exception.BizException;
import com.admin.exception.ExceptionDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 认证鉴权工具
 *
 * @author vains
 */
@Slf4j
public class SecurityUtil {



    private SecurityUtil() {
        // 禁止实例化工具类
        throw new UnsupportedOperationException("Utility classes cannot be instantiated.");
    }




    /**
     * 认证与鉴权失败回调
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param e        具体的异常信息
     */
    public static void exceptionHandler(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Throwable e) throws IOException {
        Throwable cause = e.getCause();
        if (e instanceof InsufficientAuthenticationException) {
            // 没有token
            ExceptionDetail detail = new ExceptionDetail();
            detail.setError("会话已过期");
            detail.setMessage("会话已过期");
            detail.setException("会话已过期");
            detail.setBizErrorCode("401");

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonUtil.toJson(detail));
            response.getWriter().flush();
        } else if (cause instanceof ResponseStatusException res) {
            ExceptionDetail detail = new ExceptionDetail();
            detail.setError(res.getReason());
            detail.setMessage(res.getReason());
            detail.setException(res.getReason());
            detail.setBizErrorCode(res.getStatusCode().toString());

            response.setStatus(res.getStatusCode().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonUtil.toJson(detail));
            response.getWriter().flush();
        }
        else if (cause instanceof BizException biz) {
            String message = biz.getMessage();

            ExceptionDetail detail = new ExceptionDetail();
            detail.setError(message);
            detail.setMessage(message);
            detail.setException(biz.getMessage());

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonUtil.toJson(detail));
            response.getWriter().flush();
        } else if (e instanceof OAuth2AuthenticationException exception){
            OAuth2Error error = exception.getError();
            ExceptionDetail detail = new ExceptionDetail();
            detail.setError(error.getErrorCode());
            detail.setMessage(error.getErrorCode());
            detail.setException(e.getMessage());
            detail.setBizErrorCode(error.getErrorCode());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonUtil.toJson(detail));
            response.getWriter().flush();
        } else {
            ExceptionDetail detail = new ExceptionDetail();
            detail.setError("系统异常！");
            detail.setMessage("系统异常！");
            detail.setException(e.getMessage());
            detail.setBizErrorCode("SYSTEM_ERROR");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonUtil.toJson(detail));
            response.getWriter().flush();
        }

    }

}
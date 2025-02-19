package com.admin.common.util;


import cn.hutool.http.ContentType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */

public final class HttpServletUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpServletUtil.class);

    public static void writeException(HttpServletResponse response, String content) {
        try {
            response.reset();
            response.setStatus(200);
            response.setContentType(ContentType.TEXT_HTML.getValue());
            response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            response.getWriter().write(String.format("<script>alert('%s');</script>", content));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public static void setHttpServletResponseFileName(HttpServletRequest request, HttpServletResponse response, String name) throws UnsupportedEncodingException {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.contains("Firefox")) {
            name = new String(URLEncoder.encode(name, "UTF-8").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        } else {
            name = URLEncoder.encode(name, "UTF-8");
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + name);
        response.setContentType("application/octet-stream;charset=UTF-8");
    }

    private HttpServletUtil() {
    }

    public static String getHeaderMessage(String key) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getHeader(key) : null;
    }

    public static String getRequestUri() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getRequestURI() : null;
    }

    public static String getRequestParams(String key) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getParameter(key) : null;
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }


}

package com.admin.utils;


import com.admin.common.util.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/10
 */
@Slf4j
public class HttpResponseUtil {


    /**
     * http 响应输出
     *
     * @param response response
     * @param status status
     * @param obj obj
     */
    public static void out(HttpServletResponse response, int status, Object obj) {
        PrintWriter out = null;
        try {
            response.setStatus(status);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.println(JsonUtil.toJson(obj));
        } catch (Exception e) {
            log.error("输出JSON出现未知异常！", e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}

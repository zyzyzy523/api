package com.admin.async;


import com.admin.common.dto.CustomUser;
import com.admin.filter.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2020/8/7
 */
@Slf4j
public class ContextUtil implements ContextProcesser {

    @Override
    public void clearContext() {
        if (log.isTraceEnabled()) {
            log.trace("\n 清理子线程的上下文");
        }
        try {
            UserContext.clear();
        } catch (Exception e) {
            log.error("清理 UserContext 发生异常", e);
        }
        try {
            RequestContextHolder.resetRequestAttributes();
        } catch (Exception e) {
            log.error("清理RequestContextHolder 发生异常", e);
        }

    }

    @Override
    public Map<String, Object> getContext() {
        if (log.isTraceEnabled()) {
            log.trace("\n 获取主线程的上下文");
        }
        Map<String, Object> contexts = new HashMap<>();
        contexts.put("SecurityContext", UserContext.get());
        contexts.put("RequestAttributes", RequestContextHolder.getRequestAttributes());

        return contexts;
    }

    @Override
    public void setContext(Map<String, Object> contexts) {
        if (log.isTraceEnabled()) {
            log.trace("\n 设置子线程的上下文");
        }
        Object securityContext = contexts.get("SecurityContext");
        if (securityContext != null) {
            UserContext.clear();
            UserContext.set((CustomUser) securityContext);
        }
        Object requestAttributes = contexts.get("RequestAttributes");
        if (requestAttributes != null) {
            RequestContextHolder.setRequestAttributes((RequestAttributes) requestAttributes);
        }

    }
}

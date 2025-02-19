package com.admin.async;


import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
public interface ContextProcesser {
    void setContext(Map<String, Object> var1);

    Map<String, Object> getContext();

    void clearContext();
}

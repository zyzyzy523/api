package com.admin.filter;

import com.admin.common.dto.CustomUser;
import org.slf4j.MDC;


/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/9/21
 */
public class UserContext {

    private static final ThreadLocal<CustomUser> contextHolder = new InheritableThreadLocal<>();


    public static void clear() {
        contextHolder.remove();
        MDC.remove("userId");
        MDC.remove("tenantId");
    }

    public static void set(CustomUser principalLite) {

        contextHolder.set(principalLite);
        if (principalLite != null) {
            if (principalLite.getId() != null) {
                MDC.put("userId", principalLite.getId().toString());
            }
            if (principalLite.getTenantId() != null) {
                MDC.put("tenantId", principalLite.getTenantId().toString());
            }
        }
    }

    public static CustomUser get() {
        return contextHolder.get();
    }
}

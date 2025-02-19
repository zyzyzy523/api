package com.admin.exception;


/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
public interface Msg {

    String SYS_DATA_LOCKED = "资源正忙，请稍后重试";
    String SYS_VERSION_NUMBER_CHANGED = "数据版本不一致";
    String SYSTEM_EXCEPTION = "系统异常，请联系管理员";
    String ERROR_AUTH_CLIENT = "无效的客户端";

    String USER_LOGIN_ERROR = "用户名或密码错误";
    String USER_NOT_ACTIVATED = "用户未激活";
}

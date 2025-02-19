package com.admin.common.context;


import com.admin.common.dto.CustomUser;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
public class Login {

    public static CustomUser user() {
        return new CustomUser();
    }
}

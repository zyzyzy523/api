package com.admin.base.service;


import com.admin.auth.user.PrincipalLite;
import com.admin.base.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/6
 */
public interface UserService extends IService<User> {

    PrincipalLite getByPhone(String phone);
}

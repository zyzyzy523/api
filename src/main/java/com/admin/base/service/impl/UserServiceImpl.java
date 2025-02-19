package com.admin.base.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.admin.auth.user.PrincipalLite;
import com.admin.base.entity.User;
import com.admin.base.mapper.UserMapper;
import com.admin.base.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/6
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public PrincipalLite getByPhone(String phone) {
        List<User> list = lambdaQuery().eq(User::getPhoneNumber, phone).list();
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        PrincipalLite principalLite = new PrincipalLite();
        User user = list.get(0);
        principalLite.setId(user.getId());
        principalLite.setLogin(user.getPhoneNumber());
        principalLite.setPassword(user.getPasswd());
        principalLite.setActivated(true);
        principalLite.setMobile(user.getPhoneNumber());
        principalLite.setUserCode(user.getCode());
        principalLite.setUserName(user.getName());
        return principalLite;
    }


}

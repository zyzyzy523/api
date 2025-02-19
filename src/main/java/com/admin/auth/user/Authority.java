package com.admin.auth.user;

import com.admin.common.dto.CustomRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/9/21
 */
@Data
public class Authority extends CustomRole implements GrantedAuthority {




    public Authority() {
    }

    public Authority(String roleCode) {
        this.roleCode = roleCode;
    }

    public Authority(String roleName, String roleCode, Long id) {
        this.roleName = roleName;
        this.roleCode = roleCode;
        this.id = id;
    }


    @Override
    public String getAuthority() {
        return this.roleCode;
    }
}

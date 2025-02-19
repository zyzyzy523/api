package com.admin.auth.user;


import com.admin.common.dto.CustomRole;
import com.admin.common.dto.CustomUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/9/21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrincipalLite extends CustomUser implements UserDetails {
    private static final long serialVersionUID = 1;
    @JsonIgnore
    protected Set<Authority> authorities = new HashSet<>();
    protected String password;

    public PrincipalLite(CustomUser customUserDetail) {
        this.id = customUserDetail.getId();
        this.language = customUserDetail.getLanguage();
        this.login = customUserDetail.getLogin();
        this.status = customUserDetail.getStatus();
        this.tenantId = customUserDetail.getTenantId();
        this.userName = customUserDetail.getUserName();
        this.email = customUserDetail.getEmail();
        this.mobile = customUserDetail.getMobile();
        this.authorities = customUserDetail.getRoles() ==
                null ? new HashSet<>() :
                customUserDetail.getRoles().stream().map(v -> new Authority(v.getRoleName(), v.getRoleCode(),
                        v.getId())).collect(Collectors.toSet());
        this.activated = Boolean.TRUE.equals(customUserDetail.getActivated());
        this.userCode = customUserDetail.getUserCode();
        this.roles = customUserDetail.getRoles();
        this.clientId = customUserDetail.getClientId();
        this.tenantCode = customUserDetail.getTenantCode();

    }
    /**
     * 请求id用于记录审计日志请求时间
     */
    protected Long requestId;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.activated);
    }

    @JsonIgnore
    public Set<Authority> getAuthRoles() {
        return this.authorities;
    }


}

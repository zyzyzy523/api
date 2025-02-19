package com.admin.common.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
@Data
public class CustomUser implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Long id;
    protected String login;
    protected Integer status;
    protected String language;
    protected Long tenantId;
    protected String tenantCode;
    protected String userName;
    protected String email;
    protected String mobile;
    protected Boolean activated;
    protected String userCode;
    protected String clientId;
    @JsonIgnore
    protected List<CustomRole> roles;
}

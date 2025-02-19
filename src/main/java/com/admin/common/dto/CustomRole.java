package com.admin.common.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
@Data
public class CustomRole implements Serializable {

    protected Long id;
    protected String roleCode;
    protected String roleName;
}

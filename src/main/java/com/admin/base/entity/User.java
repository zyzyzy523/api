package com.admin.base.entity;


import com.admin.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/6
 */
@TableName("sys_user")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class User extends BaseEntity {

    private Long tenantId;

    private String code;
    private String name;
    private String nickName;
    private String passwd;
    private String phoneNumber;
    private String email;
}

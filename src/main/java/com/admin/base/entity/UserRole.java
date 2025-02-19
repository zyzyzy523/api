package com.admin.base.entity;


import com.admin.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/6
 */
@TableName("sys_user_role")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserRole extends BaseEntity {
    private Long tenantId;
    private Long userId;
    private Long roleId;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private ZonedDateTime dateFrom;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private ZonedDateTime dateTo;
}

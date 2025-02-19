package com.admin.base.entity;


import com.admin.base.enums.MenuTypeEnum;
import com.admin.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("sys_menu")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Menu extends BaseEntity {

    private Long tenantId;
    private String code;
    private String name;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long parentId;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String icon;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String url;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String router;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String filePath;
    private Boolean show;
    private Integer orderNumber;
    private MenuTypeEnum type;
}

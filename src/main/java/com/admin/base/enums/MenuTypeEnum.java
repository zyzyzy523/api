package com.admin.base.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/6
 */
@Getter
public enum MenuTypeEnum implements IEnum<Integer> {

    CONTENT(1, "目录"),
    FUNCTION(2, "菜单"),
    ;

    private final int code;

    private final String desc;

    MenuTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    @Override
    public Integer getValue() {
        return code;
    }
}

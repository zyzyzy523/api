package com.admin.common.web;


import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
@Data
@Accessors(chain = true)
public class R<T> {

    private Integer code;

    private T data;

    private Long total;

    private String msg;


    public static <T> R<T> ok(T data) {
        R<T> result = new R<>();
        result.setCode(HttpStatus.HTTP_OK);
        result.setData(data);
        return result;
    }

    public static <T> R<Collection<T>> ok(Page<T> page) {
        R<Collection<T>> result = new R<>();
        result.setCode(HttpStatus.HTTP_OK);
        result.setData(page.getRecords());
        result.setTotal(page.getTotal());
        return result;
    }

    public static <T> R<T> fail(String msg) {
        R<T> result = new R<>();
        result.setCode(HttpStatus.HTTP_INTERNAL_ERROR);
        result.setMsg(msg);
        return result;
    }


}

package com.admin.mybatis.plugin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/1/25
 */
public class CuxPaginationInnerInterceptor extends PaginationInnerInterceptor {


    protected void handlerOverflow(IPage<?> page) {
        page.setCurrent(page.getPages());
    }
}

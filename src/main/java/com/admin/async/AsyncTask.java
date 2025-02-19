package com.admin.async;

import java.util.UUID;

/**
 * <p>
 * 线程异步执行的接口
 * </p>
 *
 * @author bin.xie
 * @date 2020/8/7
 */
public interface AsyncTask<T> {
    /**
     * 当前task的名称
     *
     * @return
     */
    default String taskName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 异步执行的方法
     *
     * @return T
     */
    T doExecute();
}
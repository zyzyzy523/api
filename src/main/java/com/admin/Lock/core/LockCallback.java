package com.admin.Lock.core;


/**
 * <p>
 * 分布式锁回调
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/29
 */
public interface LockCallback<T> {
    /**
     * 调用者必须在此方法中实现需要加分布式锁的业务逻辑
     *
     * @return
     * @throws Exception
     */
    T process() throws Exception;

}

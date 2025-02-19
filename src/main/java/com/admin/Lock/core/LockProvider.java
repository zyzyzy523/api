package com.admin.Lock.core;

import org.redisson.api.RLock;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 分布式锁方法接口
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/29
 */
public interface LockProvider {

    /**
     * 根据key获取单个 RLock
     *
     * @param lockKey
     * @return
     */
    RLock getRLockByKey(String lockKey);


    /**
     * 根据keys获取 RLock
     *
     * @param lockKeys
     * @return
     */
    RLock[] getRLocksByListKey(List<String> lockKeys);

    /**
     * 手工使用分布式锁 锁集合
     *
     * @param lockKey
     * @param callback
     * @param waitTime
     * @param leaseTime
     * @param timeUnit
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T tryLockList(List<String> lockKey, LockCallback<T> callback, long waitTime, long leaseTime, TimeUnit timeUnit)
            throws Exception;


    /**
     * 使用分布式锁。自定义锁的超时时间
     *
     * @param lockKey
     * @param callback
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T tryLockList(List<String> lockKey, LockCallback<T> callback) throws Exception;

}

package com.admin.Lock.core;


import com.admin.Lock.config.RedissonClientConfig;
import com.admin.exception.BizException;
import com.admin.exception.Msg;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 分布式锁实现
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/29
 */
public class LockProviderImpl implements LockProvider {
    private static final long DEFAULT_TIMEOUT = 60 * 10 * 1000;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
    private static final long DEFAULT_WAIT_TIME = -1;


    private RedissonClient redisson;

    private long sleepTime;
    public LockProviderImpl() {
    }

    public LockProviderImpl(RedissonClient redisson, long sleepTime) {
        this.redisson = redisson;
        this.sleepTime = sleepTime;
    }


    @Override
    public RLock getRLockByKey(String lockKey) {

        return redisson.getLock(lockKey);
    }


    @Override
    public <T> T tryLockList(List<String> lockKey, LockCallback<T> callback, long waitTime, long leaseTime, TimeUnit timeUnit) throws Exception {
        List<String> lockKeys = lockKey.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(lockKeys)) {
            return callback.process();
        }

        RLock[] rlockByList = getRLocksByListKey(lockKeys);

        RedissonMultiLock lock = null;
        boolean locked = false;
        try {
            lock = new RedissonMultiLock(rlockByList);
            if (!lock.tryLock(-1, leaseTime, TimeUnit.MILLISECONDS)) {
                throw new BizException(Msg.SYS_DATA_LOCKED);
            }
            locked = true;
            if (RedissonClientConfig.isCluster()) {
                Thread.sleep(sleepTime);
            }
            return callback.process();
        } finally {
            if (lock != null && locked) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T tryLockList(List<String> lockKey, LockCallback<T> callback) throws Exception {
        return tryLockList(lockKey, callback, DEFAULT_WAIT_TIME, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    @Override
    public RLock[] getRLocksByListKey(List<String> lockKeys) {
        RLock[] rLock = new RLock[lockKeys.size()];
        for (int i = 0; i < lockKeys.size(); i++) {
            rLock[i] = redisson.getLock(lockKeys.get(i));
        }
        return rLock;
    }

}

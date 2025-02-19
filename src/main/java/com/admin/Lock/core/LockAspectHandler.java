package com.admin.Lock.core;



import com.admin.Lock.LockInfo;
import com.admin.Lock.annotation.Lock;
import com.admin.Lock.config.LockProperties;
import com.admin.Lock.config.RedissonClientConfig;
import com.admin.exception.BizException;
import com.admin.exception.Msg;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.RedissonLock;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 给注解添加切面
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/01/11
 */
@Aspect
@Component
public class LockAspectHandler implements Ordered {

    @Autowired
    private LockProvider lock4hProvider;

    @Autowired
    private LockInfoProvider lockInfoProvider;

    @Autowired
    private LockProperties lockProperties;

    @Around(value = "@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        LockInfo lockInfo = lockInfoProvider.getLockInfo(joinPoint, lock);
        switch (lockInfo.getType()) {
            case LOCK:
                return lock(lockInfo, joinPoint);
            case TRY_LOCK_LIST:
                return tryLockList(lockInfo, joinPoint);
            default:
                return tryLock(lockInfo, joinPoint);
        }
    }

    private Object lock(LockInfo lockInfo, ProceedingJoinPoint joinPoint) throws Throwable {
        if (!StringUtils.hasText(lockInfo.getKey())){
            return joinPoint.proceed();
        }
        RLock lock = null;
        try {
            lock = lock4hProvider.getRLockByKey(lockInfo.getKey());
            lock.lock(lockInfo.getLeaseTime(), TimeUnit.MILLISECONDS);
            if (RedissonClientConfig.isCluster()) {
                Thread.sleep(lockProperties.getSleeptime());
            }
            return joinPoint.proceed();
        } finally {
            if (lock != null) {
                if (lock instanceof RedissonLock) {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                } else {
                    lock.unlock();
                }
            }


        }
    }

    private Object tryLock(LockInfo lockInfo, ProceedingJoinPoint joinPoint) throws Throwable {
        if (!StringUtils.hasText(lockInfo.getKey())){
            return joinPoint.proceed();
        }
        RedissonRedLock lock = null;
        try {
            lock = new RedissonRedLock(lock4hProvider.getRLockByKey(lockInfo.getKey()));
            if (lock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.MILLISECONDS)) {
                if (RedissonClientConfig.isCluster()) {
                    Thread.sleep(lockProperties.getSleeptime());
                }
                return joinPoint.proceed();
            } else {
                throw new BizException(Msg.SYS_DATA_LOCKED);
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private Object tryLockList(LockInfo lockInfo, ProceedingJoinPoint joinPoint) throws Throwable {
        if (CollectionUtils.isEmpty(lockInfo.getListKeys())) {
            return joinPoint.proceed();
        }
        RLock[] locks = lock4hProvider.getRLocksByListKey(lockInfo.getListKeys());
        RedissonMultiLock lock = null;
        boolean locked = false;
        try {
            lock = new RedissonMultiLock(locks);
            if (!lock.tryLock(-1, lockInfo.getLeaseTime(), TimeUnit.MILLISECONDS)) {
                throw new BizException(Msg.SYS_DATA_LOCKED);
            }
            locked = true;
            if (RedissonClientConfig.isCluster()) {
                Thread.sleep(lockProperties.getSleeptime());
            }
            return joinPoint.proceed();
        } finally {
            if (lock != null && locked) {
                lock.unlock();
            }
        }
    }

    /*private Object lockList(LockInfo lockInfo, ProceedingJoinPoint joinPoint) throws Throwable {
        if (CollectionUtils.isEmpty(lockInfo.getListKeys())) {
            return joinPoint.proceed();
        }
        List<RedissonRedLock> locks = getRedissonRedLock(lockInfo);
        try {
            for (RedissonRedLock lock : locks) {
                lock.lock(lockInfo.getLeaseTime(), TimeUnit.MILLISECONDS);
            }
            return joinPoint.proceed();
        } finally {
            locks.forEach(e -> {
                if (e != null) {
                    e.unlock();
                }
            });
        }
    }*/


    @Override
    public int getOrder() {
        return lockProperties.getOrder();
    }
}

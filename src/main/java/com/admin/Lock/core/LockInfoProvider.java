package com.admin.Lock.core;


import com.admin.Lock.LockInfo;
import com.admin.Lock.LockType;
import com.admin.Lock.annotation.Lock;
import com.admin.Lock.config.LockProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 获取锁信息方法
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/01/11
 */
public class LockInfoProvider {

    public static final String LOCK_NAME_PREFIX = "fabric.Lock";
    public static final String LOCK_NAME_SEPARATOR = ".";


    @Autowired
    private LockProperties lockProperties;

    @Autowired
    private LockKeyProvider lockKeyProvider;

    public LockInfo getLockInfo(ProceedingJoinPoint joinPoint, Lock lock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LockType type = lock.lockType();
        long leaseTime = getLeaseTime(lock);
        long waitTime = getWaitTime(lock);
        if (type.equals(LockType.TRY_LOCK_LIST)) {
            // 锁集合
            String preKey = LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + getName(lock.name(), signature);
            List<String> businessKeyNames = lockKeyProvider.getKeyNameList(joinPoint, lock);
            if (!CollectionUtils.isEmpty(businessKeyNames)) {
                businessKeyNames = businessKeyNames.stream().map(e -> preKey + LOCK_NAME_SEPARATOR + e).collect(Collectors.toList());
            }else{
                businessKeyNames = new ArrayList<>();
            }
            return new LockInfo(type, businessKeyNames, waitTime, leaseTime);
        } else {
            // 获取参数key
            String businessKeyName = lockKeyProvider.getKeyName(joinPoint, lock);
            String lockName;
            if (StringUtils.hasText(businessKeyName)) {
                lockName = LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + getName(lock.name(), signature) + LOCK_NAME_SEPARATOR + businessKeyName;
            }else{
                lockName = null;
            }
            return new LockInfo(type, lockName, waitTime, leaseTime);
        }
    }

    /**
     * 如果注解的name没有值就为当前方法的全类名+ 方法名
     *
     * @param annotationName
     * @param signature
     * @return
     */
    private String getName(String annotationName, MethodSignature signature) {
        if (annotationName.isEmpty()) {
            return String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
        } else {
            return annotationName;
        }
    }


    private long getWaitTime(Lock lock) {
        return lock.waitTime() == Long.MIN_VALUE ?
                lockProperties.getWaitTime() : lock.waitTime();
    }

    private long getLeaseTime(Lock lock) {
        return lock.leaseTime() == Long.MIN_VALUE ?
                lockProperties.getLeaseTime() : lock.leaseTime();
    }
}

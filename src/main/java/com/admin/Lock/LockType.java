package com.admin.Lock;

/**
 * <p>
 * 锁类型
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/01/11
 */
public enum LockType {
    /**
     * 尝试获取锁。如果被锁就报错 锁单个数据 红锁
     */
    TRY_LOCK,
    /**
     * 一直等待获取锁 锁单个数据
     */
    LOCK,

    /**
     * 尝试获取锁。如果被锁就报错 锁集合 联锁
     */
    TRY_LOCK_LIST,



    LockType() {
    }

}

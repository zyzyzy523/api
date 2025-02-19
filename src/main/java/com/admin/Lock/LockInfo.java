package com.admin.Lock;


import java.util.List;

/**
 * <p>
 * 锁信息
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/01/11
 */
public class LockInfo {

    /**
     * 锁类型
     */
    private LockType type;
    /**
     * 锁的key
     */
    private String key;

    /**
     * 尝试获取锁时间(缺省值200ms),此时间根据业务需要而定,就目前大多数使用场景需要尽量小的尝试获取锁的时间,单位毫秒
     */
    private long waitTime;
    /**
     * 失效时间
     */
    private long leaseTime;

    /**
     * 要锁的集合keys
     */
    private List<String> listKeys;

    public LockInfo() {
    }

    public LockInfo(LockType type, String key, long waitTime, long leaseTime) {
        this.type = type;
        this.key = key;
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
    }

    public LockInfo(LockType type, List<String> listKeys, long waitTime, long leaseTime) {
        this.leaseTime = leaseTime;
        this.listKeys = listKeys;
        this.waitTime = waitTime;
        this.type = type;
    }

    public LockType getType() {
        return type;
    }

    public void setType(LockType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public List<String> getListKeys() {
        return listKeys;
    }

    public void setListKeys(List<String> listKeys) {
        this.listKeys = listKeys;
    }
}

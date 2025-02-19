package com.admin.mybatis.plugin;


import com.admin.common.context.Login;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.ZonedDateTime;

public class DomainObjectMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Long currentUserId = getCurrentUserId();
//        setValue("versionNumber", 1, metaObject);
        if (currentUserId != null) {
            setFieldValByName("createdBy", currentUserId, metaObject);
            setFieldValByName("lastUpdatedBy", currentUserId, metaObject);
        } else {
            setValue("createdBy", 0L, metaObject);
            setValue("lastUpdatedBy", 0L, metaObject);
        }
        ZonedDateTime now = ZonedDateTime.now();
        setFieldValByName("createdDate", now, metaObject);
        setFieldValByName("lastUpdatedDate", now, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            // 当前用户不存在，就不更新其 lastUpdatedBy
            setFieldValByName("lastUpdatedBy", currentUserId, metaObject);
        } else {
            setValue("lastUpdatedBy", 0L, metaObject);
        }
        setFieldValByName("lastUpdatedDate", ZonedDateTime.now(), metaObject);
    }

    private void setValue(String fieldName, Object value, MetaObject metaObject) {
        Object field = getFieldValByName(fieldName, metaObject);
        if (field == null && value != null) {
            setFieldValByName(fieldName, value, metaObject);
        }
    }

    private Long getCurrentUserId() {

        Long currentUserID = null;
        try {
            currentUserID = Login.user().getId();
        } catch (Exception e) {
        }
        return currentUserID;
    }
}

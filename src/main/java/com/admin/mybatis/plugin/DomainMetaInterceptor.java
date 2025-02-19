package com.admin.mybatis.plugin;


import com.admin.common.entity.BaseEntity;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;

import java.util.Properties;

/**
 * @author kai.zhang05@
 * @create 2018/9/4 21:05
 * @remark 公用字段默认值设置
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class DomainMetaInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        if (SqlCommandType.INSERT.equals(sqlCommandType)) {
            if (parameter != null) {
                Class classParameter = parameter.getClass();
                if (classParameter != null) {
                    if (checkParameterMeta(classParameter, parameter)) {
                        // 如果为插入或者更新，需要更新公用字段
                        Reflector reflector = new Reflector(classParameter);
                        Invoker versionNumber = reflector.getSetInvoker("versionNumber");
                        versionNumber.invoke(parameter, new Object[]{1});
                        try {
                            Invoker enabledGet = reflector.getGetInvoker("enabled");
                            Object invoke = enabledGet.invoke(parameter, new Object[]{});
                            if (invoke == null) {
                                reflector.getSetInvoker("enabled").invoke(parameter, new Object[]{Boolean.TRUE});
                            }
                        } catch (ReflectionException e) {

                        }
                        try {
                            Invoker deletedGet = reflector.getGetInvoker("deleted");
                            Object invoke = deletedGet.invoke(parameter, new Object[]{});
                            if (invoke == null) {
                                reflector.getSetInvoker("deleted").invoke(parameter, new Object[]{Boolean.FALSE});
                            }
                        } catch (ReflectionException e) {

                        }
                    }
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 判断参数属性
     *
     * @param classParameter
     * @param parameter
     * @return
     */
    private Boolean checkParameterMeta(Class classParameter, Object parameter) {
        return BaseEntity.class.isAssignableFrom(classParameter);
    }


}

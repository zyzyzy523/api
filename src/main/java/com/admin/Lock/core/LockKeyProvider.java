package com.admin.Lock.core;


import com.admin.Lock.annotation.Lock;
import com.admin.Lock.annotation.LockKey;
import com.admin.Lock.annotation.LockListKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 获取用户定义lockKey
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/01/11
 */
public class LockKeyProvider {

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public String getKeyName(ProceedingJoinPoint joinPoint, Lock lock) {
        Method method = getMethod(joinPoint);
        // 根据表达式获取key
        List<String> definitionKeys = getSpelDefinitionKey(lock.keys(), method, joinPoint.getArgs());
        List<String> keyList = new ArrayList<>(definitionKeys);
        // 根据参数注解获取
        List<String> parameterKeys = getParameterKey(method.getParameters(), joinPoint.getArgs());
        keyList.addAll(parameterKeys);
        if (CollectionUtils.isEmpty(keyList)){
            return null;
        }
        return StringUtils.collectionToDelimitedString(keyList, "", "-", "").substring(1);
    }

    public List<String> getKeyNameList(ProceedingJoinPoint joinPoint, Lock lock) {
        Method method = getMethod(joinPoint);
        // 根据表达式获取listKey
        List<String> definitionKeys = getSpelDefinitionKeyList(lock.listKey(), method, joinPoint.getArgs());
        Set<String> keyList = new HashSet<>(definitionKeys);
        // 根据参数注解获取
        List<String> parameterKeys = getParameterKeyList(method.getParameters(), joinPoint.getArgs());
        keyList.addAll(parameterKeys);
        return new ArrayList<>(keyList);
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(),
                        method.getParameterTypes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return method;
    }

    private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        List<String> definitionKeyList = new ArrayList<>();
        for (String definitionKey : definitionKeys) {
            if (definitionKey != null && !definitionKey.isEmpty()) {
                EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, NAME_DISCOVERER);
                Object value = PARSER.parseExpression(definitionKey).getValue(context);
                if (null != value) {
                    definitionKeyList.add(value.toString());
                }
            }
        }
        return definitionKeyList;
    }

    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
        List<String> parameterKey = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(LockKey.class) != null) {
                LockKey keyAnnotation = parameters[i].getAnnotation(LockKey.class);
                if (keyAnnotation.value().isEmpty()) {
                    parameterKey.add(parameterValues[i].toString());
                } else {
                    StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);
                    Object value = PARSER.parseExpression(keyAnnotation.value()).getValue(context);
                    if (null != value) {
                        parameterKey.add(value.toString());
                    }
                }
            }
        }
        return parameterKey;
    }

    @SuppressWarnings("unchecked")
    private List<String> getParameterKeyList(Parameter[] parameters, Object[] parameterValues) {
        List<String> parameterKey = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Object parameterValue = parameterValues[i];
            if (parameterValue instanceof Collection &&
                    parameters[i].getAnnotation(LockListKey.class) != null) {
                LockListKey keyAnnotation = parameters[i].getAnnotation(LockListKey.class);
                StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);
                String annotationExpression;
                if (keyAnnotation.value().isEmpty()) {
                    annotationExpression = "#root";
                } else {
                    annotationExpression = keyAnnotation.value();
                }
                List<Object> value = PARSER.parseExpression(annotationExpression).getValue(context, List.class);
                if (!CollectionUtils.isEmpty(value)) {
                    parameterKey.addAll(getNonNullObject(value));
                }
            }
        }
        return parameterKey;
    }

    @SuppressWarnings("unchecked")
    private List<String> getSpelDefinitionKeyList(String definitionKey, Method method, Object[] parameterValues) {
        List<String> definitionKeyList = new ArrayList<>();

        if (StringUtils.hasText(definitionKey)) {
            EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, NAME_DISCOVERER);
            List<Object> keys = PARSER.parseExpression(definitionKey).getValue(context, List.class);
            if (!CollectionUtils.isEmpty(keys)) {
                definitionKeyList.addAll(getNonNullObject(keys));
            }
        }
        return definitionKeyList;
    }

    private List<String> getNonNullObject(List<Object> objects) {
        // 如果集合元素都为null 则不添加key
        boolean allMatch = objects.stream().anyMatch(Objects::isNull);
        if (!allMatch) {
            // 排除null 元素
            return objects
                    .stream()
                    .filter(Objects::nonNull)
                    .map(Objects::toString)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

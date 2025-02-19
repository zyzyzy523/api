package com.admin.common.util;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * <p>
 *  json工具类
 * </p>
 *
 * @author bin.xie
 * @since 2019/12/3
 */
@Slf4j
public class JsonUtil extends JSONUtil {

    protected JsonUtil() {

    }

    protected static final ObjectMapper OBJECT_MAPPER;

    static {
        JsonMapper.Builder builder = JsonMapper.builder();
        builder.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS);
        OBJECT_MAPPER = builder.build();
        OBJECT_MAPPER.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();

        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(Long.TYPE, new ToStringSerializer());
        OBJECT_MAPPER.registerModule(module);
    }

    /**
     * 将对象转换为Json字符串
     * @param obj 对象
     * @return json
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象转换为 Bytes
     * @param obj 对象
     * @return json
     */
    public static byte[] toBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     *  将json字符串转换为javabean
     * @param json  json字符串
     * @param clazz java bean
     * @return T
     */
    public static <T>  T fromJson(String json, Class<T> clazz) {

        return fromJson(json.getBytes(StandardCharsets.UTF_8), clazz);
    }

    /**
     *  将json字符串转换为javabean
     * @param json  json字符串
     * @param clazz java bean
     * @return T
     */
    public static <T> T fromJson(byte[] json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("JSON[{}]，转化出错!", new String(json, StandardCharsets.UTF_8), e);
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> toMap(String json) {
        try {
            return JsonUtil.fromJson(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("JSON[{}]，转化出错!", json, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json数组转换为java集合对象
     * @param json json
     * @return List
     */
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<T>>() {
                @Override
                public Type getType() {
                    return OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
                }
            });
        } catch (IOException e) {
            log.error("JSON[{}]，转化出错!", json, e);
            throw new RuntimeException(e);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }









    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("JSON[{}]，转化出错!", json, e);
            throw new RuntimeException(e);
        }
    }



    /**
     * 根据表达式获取json的值
     * @param json json字符串
     * @param expression 表达式 如：id
     * @return Object
     */
    public static Object getByPath(String json, String expression) {
        return JSONUtil.getByPath(JSONUtil.parse(json), expression);
    }

    /**
     * 根据表达式设置json的值
     * @param json json字符串
     * @param expression 表达式 如：id
     * @param value 值
     */
    public static String putByPath(String json, String expression, Object value) {
        JSON parse = JSONUtil.parse(json);
        JSONUtil.putByPath(parse, expression, value);
        return parse.toString();
    }


    /**
     * 读取JSON数据，将JSON数据转换为对象
     *
     * @param json json
     * @param keyMap 对象数据映射 key 属性的key value json的path
     * @return
     */
    public static JSON parseLevel(String json, Map<String, String> keyMap) {

        return parseLevel(JSONUtil.parse(json), keyMap, null);
    }

    public static JSON parseLevel(String json, Map<String, String> keyMap, Map<String, Object> defaultValueMap) {
        return parseLevel(JSONUtil.parse(json), keyMap, defaultValueMap);
    }

    public static JSON parseLevel(JSON json, Map<String, String> keyMap) {
        return parseLevel(json, keyMap, null);
    }
    public static JSON parseLevel(JSON parse, Map<String, String> keyMap, Map<String, Object> defaultValueMap) {
        if (parse == null) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        if (parse instanceof cn.hutool.json.JSONArray) {
            // JSON ARRAY
            Stack<Map<String, Object>> stack = new Stack<>();
            traversing((cn.hutool.json.JSONArray)parse, o -> {
                if (o instanceof cn.hutool.json.JSONObject) {
                    cn.hutool.json.JSONObject item = (cn.hutool.json.JSONObject) o;
                    Map<String, Object> tmp = new HashMap<>(keyMap.size());
                    keyMap.forEach((k, v) -> tmp.put(k ,JSONUtil.getByPath(item, v)));
                    stack.push(tmp);
                } else if (o instanceof cn.hutool.json.JSONArray) {
                    // 二维数组，直接取
                    cn.hutool.json.JSONArray item = (cn.hutool.json.JSONArray) o;
                    Map<String, Object> tmp = new HashMap<>(keyMap.size());
                    keyMap.forEach((k, v) -> tmp.put(k ,JSONUtil.getByPath(item, v)));
                    stack.push(tmp);
                }
            });
            while (!stack.isEmpty()) {
                Map<String, Object> item = stack.pop();
                add(result, item);
            }
            if (MapUtil.isNotEmpty(defaultValueMap)) {
                result.forEach(item -> {
                    Set<String> strings = item.keySet();
                    strings.forEach(key -> {
                        if (item.get(key) == null) {
                            item.put(key, defaultValueMap.get(key));
                        }
                    });
                });
            }
            return JSONUtil.parse(result);
        } else {
            // JSON OBJECT
            cn.hutool.json.JSONObject item = (cn.hutool.json.JSONObject) parse;
            Map<String, Object> tmp = new HashMap<>(keyMap.size());
            keyMap.forEach((k, v) -> tmp.put(k, JSONUtil.getByPath(item, v)));
            add(result, tmp);
            return JSONUtil.parse(result);
        }

    }


    private static void add(List<Map<String, Object>> result, Map<String, Object> items) {
        Map<String, Object> filter = MapUtil.filter(items, (entry) -> entry.getValue() instanceof List);
        if (CollUtil.isEmpty(filter)) {
            // 说明没有嵌套的
            result.add(ObjectUtil.clone(items));
        } else {
            // 说明有嵌套的
            Map<String, Object> objectMap = MapUtil.filter(items, (entry) -> !(entry.getValue() instanceof List));

            // 这个map是常量的值
            int size = filter.size();
            Map<String, Object> map = ObjectUtil.clone(objectMap);
            if (size == 1) {
                HashMap<String, Object> temp = new HashMap<>(map);
                List<Object> valueList = (List<Object>) new ArrayList<>(filter.values()).get(0);
                for (Object o : valueList) {
                    temp.put(new ArrayList<>(filter.keySet()).get(0), o);
                    add(result, temp);
                }
            } else {
                Set<Map.Entry<String, Object>> entries = filter.entrySet();
                int maxLength = 0;
                for (Map.Entry<String, Object> entry : entries) {
                    List<Object> valueList = (List<Object>) entry.getValue();
                    maxLength = Math.max(maxLength, valueList.size());
                }
                for (int i = 0; i < maxLength; i++) {
                    HashMap<String, Object> temp = new HashMap<>(map);
                    for (Map.Entry<String, Object> entry : entries) {
                        List<Object> valueList = (List<Object>) entry.getValue();
                        if (valueList.size() > i) {
                            Object o1 = valueList.get(i);
                            temp.put(entry.getKey(), o1);
                        } else {
                            temp.put(entry.getKey(), null);
                        }
                    }
                    add(result, temp);
                }
            }

        }
    }

    private static <T> void traversing(cn.hutool.json.JSONArray root, Consumer<Object> behavior) {
        Stack<Object> stack = new Stack<>();
        root.forEach(stack::push);
        while (!stack.isEmpty()) {
            Object o = stack.pop();
            behavior.accept(o);
        }
    }

}

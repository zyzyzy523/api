package com.admin.serializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

/**
 * <p>
 * {@link StringDeserializer}
 * </p>
 *
 * @author bin.xie
 * @since 2021/10/21
 */
public class CuxStringDeserializer extends StringDeserializer {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return StrUtil.trimToNull(super.deserialize(p, ctxt));
    }
}
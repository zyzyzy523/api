package com.admin.config;

import com.admin.serializer.CuxStringDeserializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JacksonConfiguration {

    @Bean
    public Module cuxJavaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(Long.TYPE, new ToStringSerializer());
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer cuxCustomizer() {
        return v -> v
                .deserializerByType(String.class, new CuxStringDeserializer())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .findModulesViaServiceLoader(true);
    }





    /*@Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        // 给JacksonUtil设置实现类
        JsonHelper.setJsonService(new JsonServiceImpl());
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(JSONObject.class, new JSONObjectSerializer());
        module.addDeserializer(JSONObject.class, new JSONObjectDeserializer());
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(Long.TYPE, new ToStringSerializer());
        return new Jackson2ObjectMapperBuilder()
                .deserializerByType(String.class, new StdScalarDeserializer<String>(String.class) {
                    @Override
                    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException,
                    JsonProcessingException {
                        String value = p.getValueAsString();
                        return StringUtils.trimToNull(value);
                    }
                })
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .findModulesViaServiceLoader(true)
                .modulesToInstall(module);
    }*/
}


package com.admin.config;

import cn.hutool.core.collection.CollUtil;
import com.admin.common.util.ExecutorUtil;
import com.admin.redis.RedisMessageListenerHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.support.NullValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.List;


@Configuration
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties({RedisProperties.class})
public class DefaultRedisConfiguration {
    @Value("${spring.redis.serialization:json}")
    private String serialization;

    @Value("${spring.redis.lettuce.protocolVersion:RESP3}")
    private String protocolVersion;

    @Autowired
    private RedisProperties redisProperties;

    private RedisProperties getProperties() {
        return redisProperties;
    }

    @Bean("defaultRedisListenerContainer")
    public RedisMessageListenerContainer defaultRedisListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            ObjectProvider<List<RedisMessageListenerHandler>> redisMessageListenerProvider,
            ObjectProvider<List<RedisTemplate<?, ?>>> redisTemplateProvider) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.setTaskExecutor(ExecutorUtil.getDefaultExecutor());
        List<RedisMessageListenerHandler> listenerHandlers = redisMessageListenerProvider.getIfAvailable();
        if (CollUtil.isNotEmpty(listenerHandlers)) {
            listenerHandlers.forEach(v -> container.addMessageListener(v.listener(), v.topics()));
        }
        List<RedisTemplate<?, ?>> ifAvailable = redisTemplateProvider.getIfAvailable();
        if (CollUtil.isNotEmpty(ifAvailable)) {
            ifAvailable.forEach(v -> setRedisTemplateInfo(v, redisConnectionFactory));
        }
        return container;
    }

    @Bean
    public LettuceClientConfigurationBuilderCustomizer redisCustomizer() {
        return builder -> builder.clientOptions(createClientOptions());
    }
    /*@Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        if (redisProperties.getCluster() != null) {
            // 支持自适应集群拓扑刷新和静态刷新源
            ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    //.enablePeriodicRefresh(Duration.ofSeconds(5))
                    .enableAllAdaptiveRefreshTriggers()
                    .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(10))
                    //.enablePeriodicRefresh(Duration.ofSeconds(10))
                    .build();
            // 超时修改为30秒
            ClusterClientOptions clusterClientOptions = ClusterClientOptions
                    .builder()
                    .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(30)))
                    //.autoReconnect(false)  是否自动重连
                    //.pingBeforeActivateConnection(Boolean.TRUE)
                    //.cancelCommandsOnReconnectFailure(Boolean.TRUE)
                    //.disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                    .topologyRefreshOptions(clusterTopologyRefreshOptions).build();
            return builder -> builder.clientOptions(clusterClientOptions);
        } else {
            return LettuceClientConfiguration.LettuceClientConfigurationBuilder::build;
        }
    }*/

    /**
     * {@link org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration#createClientOptions()}
     *
     * @return
     */
    private ClientOptions createClientOptions() {
        ClientOptions.Builder builder = initializeClientOptionsBuilder();
        Duration connectTimeout = getProperties().getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }
        if ("RESP2".equals(protocolVersion)) {
            builder.protocolVersion(ProtocolVersion.RESP2);
        }
        return builder.timeoutOptions(TimeoutOptions.enabled()).build();
    }

    private ClientOptions.Builder initializeClientOptionsBuilder() {
        if (getProperties().getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties =
                    getProperties().getLettuce().getCluster().getRefresh();
            ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder()
                    .dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
            if (refreshProperties.getPeriod() != null) {
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }
            if (refreshProperties.isAdaptive()) {
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }
            return builder.topologyRefreshOptions(refreshBuilder.build());
        }
        return ClientOptions.builder();
    }


    @Bean(name = "redisTemplate")
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    public <K, V> RedisTemplate<K, V> setRedisTemplateInfo(RedisTemplate<K, V> redisTemplate,
                                                           RedisConnectionFactory redisConnectionFactory) {
        if (redisTemplate instanceof StringRedisTemplate) {
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            return redisTemplate;
        }
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisSerializer valueSerializer = fabricRedisSerializer();
        // key-value结构
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        // hash数据结构
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        // 启用默认序列化方式
        redisTemplate.setEnableDefaultSerializer(true);
        redisTemplate.setDefaultSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisSerializer fabricRedisSerializer() {
        String json = "json";
        if (json.equalsIgnoreCase(serialization)) {
            // 使用json序列化 jdk8日期格式也没问题
            ObjectMapper om = new ObjectMapper();
            om.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // om.registerModule(new CoreJackson2Module());
            // om.registerModule(new WebJackson2Module());
            om.registerModule(new JavaTimeModule());
            om.registerModule((new SimpleModule())
                    .addSerializer(new NullValueSerializer()));
            //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
                    .builder()
                    .allowIfBaseType(Object.class)
                    .build();
            om.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            // GenericJackson2JsonRedisSerializer 替换默认序列化
            return new GenericJackson2JsonRedisSerializer(om);
        }  else {
            return new JdkSerializationRedisSerializer();
        }
    }

    protected class NullValueSerializer extends StdSerializer<NullValue> {
        private static final long serialVersionUID = 1999052150548658807L;
        private final String classIdentifier = "@class";

        NullValueSerializer() {
            super(NullValue.class);
        }

        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeStringField(this.classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }

    }

}

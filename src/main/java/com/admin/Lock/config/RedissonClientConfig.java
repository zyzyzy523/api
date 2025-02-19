package com.admin.Lock.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.BaseConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @date 2019/7/23
 */
@Configuration
@EnableConfigurationProperties({RedisProperties.class, LockProperties.class})
public class RedissonClientConfig {

    private static boolean isCluster = false;

    public static boolean isCluster() {
        return isCluster;
    }

    @Autowired
    private RedisProperties redisProperties;
    @Autowired
    private LockProperties lockProperties;
    /** 由于 RedisProperties 2.3版本才有这个配，为了兼容老项目 */
    @Value("${spring.redis.sentinel.password:}")
    private String sentinelPasswd;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redisson() throws IOException {
        Config config = null;
        BaseConfig bsc = new SentinelServersConfig();

        // 1. 使用自己的配置
        if (StringUtils.isNotBlank(lockProperties.getAddress())) {
            config = new Config();
            String prefix = "redis://";
            if (lockProperties.isSsl()) {
                prefix = "rediss://";
            }
            config.useSingleServer()
                    .setConnectionMinimumIdleSize(lockProperties.getConnectionMinimumIdleSize())
                    .setConnectionPoolSize(lockProperties.getConnectionPoolSize())
                    .setSubscriptionsPerConnection(lockProperties.getSubscriptionsPerConnection())
                    .setSubscriptionConnectionPoolSize(lockProperties.getSubscriptionConnectionPoolSize())
                    .setSubscriptionConnectionMinimumIdleSize(lockProperties.getSubscriptionConnectionMinimumIdleSize())
                    .setAddress(prefix + lockProperties.getAddress())
                    .setConnectTimeout(lockProperties.getTimeout())
                    .setDatabase(lockProperties.getDatabase())
                    .setPingConnectionInterval(lockProperties.getPingConnectionInterval())
                    .setPassword(lockProperties.getPassword());
            config.setNettyThreads(lockProperties.getNettyThreads());
            config.setThreads(lockProperties.getThreads());
            return Redisson.create(config);
        }

        Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
        long timeout = redisProperties.getTimeout() != null ? redisProperties.getTimeout().toMillis() : bsc.getTimeout();
        long connectTimeout = redisProperties.getConnectTimeout() != null ? redisProperties.getConnectTimeout().toMillis() : bsc.getConnectTimeout();

        if (redisProperties.getSentinel() != null) {
            Method nodesMethod = ReflectionUtils.findMethod(RedisProperties.Sentinel.class, "getNodes");
            Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, redisProperties.getSentinel());

            String[] nodes;
            if (nodesValue instanceof String) {
                nodes = convert(Arrays.asList(((String)nodesValue).split(",")));
            } else {
                nodes = convert((List<String>)nodesValue);
            }

            config = new Config();
            config.useSentinelServers()
                    .setSubscriptionsPerConnection(lockProperties.getSubscriptionsPerConnection())
                    .setSubscriptionConnectionPoolSize(lockProperties.getSubscriptionConnectionPoolSize())
                    .setSubscriptionConnectionMinimumIdleSize(lockProperties.getSubscriptionConnectionMinimumIdleSize())
                    .setMasterConnectionMinimumIdleSize(lockProperties.getMasterConnectionMinimumIdleSize())
                    .setMasterConnectionPoolSize(lockProperties.getMasterConnectionPoolSize())
                    .setSlaveConnectionMinimumIdleSize(lockProperties.getSlaveConnectionMinimumIdleSize())
                    .setSlaveConnectionPoolSize(lockProperties.getSlaveConnectionPoolSize())
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .addSentinelAddress(nodes)
                    .setTimeout((int)timeout)
                    .setConnectTimeout((int)connectTimeout)
                    .setDatabase(redisProperties.getDatabase())
                    .setReadMode(lockProperties.getReadMode() == null ? ReadMode.SLAVE : lockProperties.getReadMode())
                    .setPingConnectionInterval(lockProperties.getPingConnectionInterval())
                    .setPassword(StringUtils.isBlank(sentinelPasswd) ? redisProperties.getPassword() : sentinelPasswd);
        } else if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null) {
            Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, redisProperties);
            Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
            List<String> nodesObject = (List) ReflectionUtils.invokeMethod(nodesMethod, clusterObject);

            String[] nodes = convert(nodesObject);
            isCluster = true;
            config = new Config();
            config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setSubscriptionsPerConnection(lockProperties.getSubscriptionsPerConnection())
                    .setSubscriptionConnectionPoolSize(lockProperties.getSubscriptionConnectionPoolSize())
                    .setSubscriptionConnectionMinimumIdleSize(lockProperties.getSubscriptionConnectionMinimumIdleSize())
                    .setReadMode(lockProperties.getReadMode() == null ? ReadMode.SLAVE : lockProperties.getReadMode())
                    .setMasterConnectionMinimumIdleSize(lockProperties.getMasterConnectionMinimumIdleSize())
                    .setMasterConnectionPoolSize(lockProperties.getMasterConnectionPoolSize())
                    .setSlaveConnectionMinimumIdleSize(lockProperties.getSlaveConnectionMinimumIdleSize())
                    .setSlaveConnectionPoolSize(lockProperties.getSlaveConnectionPoolSize())
                    // 设置集群状态扫描时间
                    .setPingConnectionInterval(lockProperties.getPingConnectionInterval())
                    .setTimeout((int)timeout)
                    .setConnectTimeout((int)connectTimeout)
                    .setPassword(redisProperties.getPassword());
        } else {
            config = new Config();
            String prefix = "redis://";
            Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
            if (method != null && (Boolean)ReflectionUtils.invokeMethod(method, redisProperties)) {
                prefix = "rediss://";
            }
            config.useSingleServer()
                    .setConnectionMinimumIdleSize(lockProperties.getConnectionMinimumIdleSize())
                    .setConnectionPoolSize(lockProperties.getConnectionPoolSize())
                    .setSubscriptionsPerConnection(lockProperties.getSubscriptionsPerConnection())
                    .setSubscriptionConnectionPoolSize(lockProperties.getSubscriptionConnectionPoolSize())
                    .setSubscriptionConnectionMinimumIdleSize(lockProperties.getSubscriptionConnectionMinimumIdleSize())
                    .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setPingConnectionInterval(lockProperties.getPingConnectionInterval())
                    .setDatabase(redisProperties.getDatabase())
                    .setTimeout((int)timeout)
                    .setConnectTimeout((int)connectTimeout)
                    .setPassword(redisProperties.getPassword());

        }
        config.setNettyThreads(lockProperties.getNettyThreads());
        config.setThreads(lockProperties.getThreads());

        return Redisson.create(config);
    }

    private String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList<String>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith("redis://") && !node.startsWith("rediss://")) {
                nodes.add("redis://" + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[nodes.size()]);
    }

}
package com.admin.redis;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.Topic;

import java.util.Collection;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/2/25
 */
public interface RedisMessageListenerHandler {

    MessageListener listener();
    Collection<? extends Topic> topics();
}

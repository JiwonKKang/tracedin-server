package com.univ.tracedin.infra.metric.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.univ.tracedin.domain.metric.ServiceMetrics;
import com.univ.tracedin.infra.metric.messaging.listener.ServiceMetricsRedisListener;

@Configuration
@RequiredArgsConstructor
public class MetricRedisConfig {

    public static final String METRIC_CHANNEL_ID = "service-metrics";

    private final RedisConnectionFactory redisConnectionFactory;
    private final ObjectMapper objectMapper;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            MessageListenerAdapter listenerAdapter) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(METRIC_CHANNEL_ID));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(
            ServiceMetricsRedisListener serviceMetricsRedisListener) {
        return new MessageListenerAdapter(serviceMetricsRedisListener);
    }

    @Bean
    public RedisTemplate<String, ServiceMetrics> notificationTemplate() {
        final RedisTemplate<String, ServiceMetrics> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(
                new Jackson2JsonRedisSerializer<>(objectMapper, ServiceMetrics.class));
        return redisTemplate;
    }
}

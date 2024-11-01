package com.univ.tracedin.infra.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.auth.RefreshToken;

@Configuration
@RequiredArgsConstructor
public class TokenRedisConfig {

    private final RedisConnectionFactory connectionFactory;

    @Bean
    public RedisTemplate<String, RefreshToken> tokenRedisTemplate() {
        final RedisTemplate<String, RefreshToken> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}

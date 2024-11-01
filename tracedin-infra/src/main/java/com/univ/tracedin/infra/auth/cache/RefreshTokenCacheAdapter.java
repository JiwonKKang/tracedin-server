package com.univ.tracedin.infra.auth.cache;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.domain.auth.RefreshToken;
import com.univ.tracedin.domain.auth.RefreshTokenCache;
import com.univ.tracedin.domain.user.UserId;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCacheAdapter implements RefreshTokenCache {

    private final RedisTemplate<String, RefreshToken> template;

    @Override
    public void cache(RefreshToken refreshToken) {
        final String key = getKey(refreshToken.userId());
        log.info("Set Refresh Token from {} : {}", key, refreshToken);
        template.opsForValue().set(key, refreshToken);
    }

    @Nullable
    @Override
    public RefreshToken get(UserId id) {
        final String key = getKey(id);
        log.info("Get Refresh Token from {}", key);
        return template.opsForValue().get(key);
    }

    private static String getKey(UserId userId) {
        return "USER_REFRESH_TOKEN:" + userId.getValue();
    }
}

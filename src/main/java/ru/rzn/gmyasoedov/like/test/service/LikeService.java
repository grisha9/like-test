package ru.rzn.gmyasoedov.like.test.service;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

public interface LikeService {

    void like(String playerId);

    long getLikes(String playerId);
}

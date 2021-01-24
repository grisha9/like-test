package ru.rzn.gmyasoedov.like.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class RedisLikeService implements LikeService {
    static final String LIKE_PLAYER_PREFIX = "like.player:";
    private static final String PLAYER_ID_IS_EMPTY = "playerId is empty";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final StringRedisTemplate stringRedisTemplate;

    public RedisLikeService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void like(@NonNull String playerId) {
        Assert.notNull(playerId, PLAYER_ID_IS_EMPTY);
        String key = LIKE_PLAYER_PREFIX + playerId;
        stringRedisTemplate.opsForValue().increment(key);
    }

    @Override
    public long getLikes(@NonNull String playerId) {
        Assert.notNull(playerId, PLAYER_ID_IS_EMPTY);
        String likeCount = stringRedisTemplate.opsForValue().get(LIKE_PLAYER_PREFIX + playerId);
        return likeCount != null ? getLikeCount(likeCount, playerId) : 0L;
    }

    private long getLikeCount(String likeCount, String playerId) {
        try {
            return Long.valueOf(likeCount);
        } catch (NumberFormatException e) {
            logger.error("error like count key {} value {}", playerId, likeCount, e);
            return 0L;
        }
    }
}

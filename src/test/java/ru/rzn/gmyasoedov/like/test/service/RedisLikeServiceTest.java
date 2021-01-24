package ru.rzn.gmyasoedov.like.test.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.rzn.gmyasoedov.like.test.service.RedisLikeService.LIKE_PLAYER_PREFIX;

@Testcontainers
@SpringBootTest
class RedisLikeServiceTest {

    @Container
    private GenericContainer redis = new FixedHostPortGenericContainer("redis:5.0.3-alpine")
            .withFixedExposedPort(6379, 6379);

    @Autowired
    private LikeService likeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void getLikeNullArg() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            likeService.getLikes(null);
        });
    }

    @Test
    void likeNullArg() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            likeService.like(null);
        });
    }

    @Test
    void getLikeKeyNotExist() {
        String player = "playerNotExist";
        long likes = likeService.getLikes(player);
        Assertions.assertEquals(0, likes);
    }

    @Test
    void getLikeNotNumericKey() {
        String player = "playerNotNumeric";
        stringRedisTemplate.opsForValue().set(LIKE_PLAYER_PREFIX + player, "not_numeric");
        long likes = likeService.getLikes(player);
        Assertions.assertEquals(0, likes);
    }

    @Test
    void likeBaseWriteRead() {
        String player1 = "player1";
        String player2 = "player2";
        likeService.like(player1);
        likeService.like(player2);
        likeService.like(player2);

        long likes = likeService.getLikes(player1);
        Assertions.assertEquals(1, likes);
        likes = likeService.getLikes(player2);
        Assertions.assertEquals(2, likes);
    }

    @Test
    void likeConcurrentWrite() throws InterruptedException {
        String player = "player1";
        long singleThreadRequestCount = 10_000L;
        int threadCount = 100;
        Runnable action = () -> {
            for (int i = 0; i < singleThreadRequestCount; i++) {
                likeService.like(player);
                Thread.yield();
            }
        };
        List<Thread> threads = IntStream.range(0, threadCount)
                .mapToObj(i -> new Thread(action))
                .collect(Collectors.toList());

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        long likes = likeService.getLikes(player);
        Assertions.assertEquals(threadCount * singleThreadRequestCount, likes);
    }

}
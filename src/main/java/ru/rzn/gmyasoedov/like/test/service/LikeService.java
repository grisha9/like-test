package ru.rzn.gmyasoedov.like.test.service;

public interface LikeService {

    void like(String playerId);

    long getLikes(String playerId);
}

package ru.rzn.gmyasoedov.like.test.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rzn.gmyasoedov.like.test.service.LikeService;

@RestController
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/{playerId}")
    public long getLikes(@PathVariable("playerId") String playerId) {
        return likeService.getLikes(playerId);
    }

    @PostMapping("/{playerId}")
    public void like(@PathVariable("playerId") String playerId) {
        likeService.like(playerId);
    }
}

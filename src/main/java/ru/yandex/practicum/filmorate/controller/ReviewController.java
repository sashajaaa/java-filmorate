package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<Review> saveReview(@Valid @RequestBody Review review) {
        Review savedReview = reviewService.addReview(review);
        feedService.addFeed(savedReview.getUserId(), EventType.REVIEW,
                OperationType.ADD, savedReview.getReviewId());
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> bringBackReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(reviewService.findReviewById(id));
    }

    @GetMapping
    public ResponseEntity<List<Review>> loadReviewsByFilmId(@RequestParam(defaultValue = "-1") Integer filmId,
                                                            @RequestParam(defaultValue = "10") Integer count) {
        return ResponseEntity.ok(reviewService.loadReviewsByFilmId(filmId, count));
    }

    @PutMapping
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) {
        Review updatedReview = reviewService.updateReview(review);
        feedService.addFeed(updatedReview.getUserId(), EventType.REVIEW,
                OperationType.UPDATE, updatedReview.getReviewId());
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Review> removeReviewById(@PathVariable Integer id) {
        Review removedReview = reviewService.removeReviewById(id);
        feedService.addFeed(removedReview.getUserId(), EventType.REVIEW,
                OperationType.REMOVE, removedReview.getReviewId());
        return ResponseEntity.ok(removedReview);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> likeToReview(@PathVariable Integer id,
                                               @PathVariable Integer userId) {
        return ResponseEntity.ok(reviewService.userLikesReview(id, userId));
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> dislikeToReview(@PathVariable Integer id,
                                                  @PathVariable Integer userId) {
        return ResponseEntity.ok(reviewService.dislikeToReview(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> removeLike(@PathVariable Integer id,
                                             @PathVariable Integer userId) {
        return ResponseEntity.ok(reviewService.removeLike(id, userId));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> removeDislike(@PathVariable Integer id,
                                                @PathVariable Integer userId) {
        return ResponseEntity.ok(reviewService.removeDislike(id, userId));
    }
}

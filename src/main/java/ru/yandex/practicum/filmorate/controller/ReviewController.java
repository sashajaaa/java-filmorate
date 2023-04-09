package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

/**
 * ### API
 * `POST /reviews`
 * Добавление нового отзыва.
 * `PUT /reviews`
 * Редактирование уже имеющегося отзыва.
 * `DELETE /reviews/{id}`
 * Удаление уже имеющегося отзыва.
 * `GET /reviews/{id}`
 * Получение отзыва по идентификатору.
 * `GET /reviews?filmId={filmId}&count={count}`
 * Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.
 * - `PUT /reviews/{id}/like/{userId}`  — пользователь ставит лайк отзыву.
 * - `PUT /reviews/{id}/dislike/{userId}`  — пользователь ставит дизлайк отзыву.
 * - `DELETE /reviews/{id}/like/{userId}`  — пользователь удаляет лайк/дизлайк отзыву.
 * - `DELETE /reviews/{id}/dislike/{userId}`  — пользователь удаляет дизлайк отзыву.
 */

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<Review> saveReview(@Valid @RequestBody Review review) {
        Review savedReview = reviewService.addReview(review);
        feedService.addFeed(Long.valueOf(savedReview.getUserId()), Feed.EventType.REVIEW,
                Feed.OperationType.ADD, Long.valueOf(savedReview.getReviewId()));
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
        feedService.addFeed(Long.valueOf(updatedReview.getUserId()), Feed.EventType.REVIEW,
                Feed.OperationType.UPDATE, Long.valueOf(updatedReview.getReviewId()));
        return ResponseEntity.ok(updatedReview);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Review> removeReviewById(@PathVariable Integer id) {
        Review removedReview = reviewService.removeReviewById(id);
        feedService.addFeed(Long.valueOf(removedReview.getUserId()), Feed.EventType.REVIEW,
                Feed.OperationType.REMOVE, Long.valueOf(removedReview.getReviewId()));
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

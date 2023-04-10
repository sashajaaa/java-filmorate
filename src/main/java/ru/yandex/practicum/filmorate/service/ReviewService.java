package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BuildException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.InteractionReviewStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final InteractionReviewStorage<Integer, Review> repository;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review addReview(Review review) {
        containsUser(review.getUserId());
        containsFilm(review.getFilmId());
        log.info("Added review " + review);
        return (review.getUseful() == null
                ? repository.unload(review.withUseful(0))
                : repository.unload(review)).orElseThrow(
                () -> new BuildException("Save Exception Review -> " + review));
    }

    public Review findReviewById(Integer id) {
        containsReview(id);
        log.info("Requested review with id=" + id);
        return repository.findById(id);
    }

    public List<Review> loadReviewsByFilmId(Integer filmId, Integer count) {
        if (filmId > 0) {
            containsFilm(filmId);
        }
        log.info("Load review for film with id=" + filmId);
        List<Review> reviews = (filmId < 0)
                ? repository.load(count)
                : repository.load(filmId, count);
        return reviews.isEmpty() ? Collections.emptyList() : reviews;
    }

    public Review updateReview(Review review) {
        containsReview(review.getReviewId());
        containsUser(review.getUserId());
        containsFilm(review.getFilmId());
        log.info("Updated review with id=" + review.getReviewId());
        return repository.update(review);
    }

    public Review removeReviewById(Integer id) {
        containsReview(id);
        log.info("Removed review with id=" + id);
        return repository.remove(id);
    }

    public Review userLikesReview(Integer id, Integer userId) {
        containsReview(id);
        containsUser(userId);
        log.info("Added like to review from user with id=" + userId);
        return repository.like(id, userId);
    }

    public Review dislikeToReview(Integer id, Integer userId) {
        containsReview(id);
        containsUser(userId);
        log.info("Added dislike to review from user with id=" + userId);
        return repository.dislike(id, userId);
    }

    public Review removeLike(Integer id, Integer userId) {
        containsReview(id);
        containsUser(userId);
        log.info("Removed like from review from user with id=" + userId);
        return repository.removeLike(id, userId);
    }

    public Review removeDislike(Integer id, Integer userId) {
        containsReview(id);
        containsUser(userId);
        log.info("Removed dislike from review from user with id=" + userId);
        return repository.removeDislike(id, userId);
    }

    private void containsUser(Integer userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found. ");
        }
    }

    private void containsFilm(Integer filmId) {
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
    }

    private void containsReview(Integer reviewId) {
        if (!repository.containsReview(reviewId)) {
            throw new NotFoundException(String.format("Review by id '%d' not found", reviewId));
        }
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BuildException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.InteractionReviewRepository;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final InteractionReviewRepository<Integer, Review> repository;
	private final FilmStorage filmStorage;
	private final UserStorage userStorage;

	public Review addReview(Review review) {
		throwUserNotFoundException(review.getUserId());
		throwFilmNotFoundException(review.getFilmId());

		return (review.getUseful() == null
				? repository.unload(review.withUseful(0))
				: repository.unload(review)).orElseThrow(
						() -> new BuildException("Save Exception Review -> " + review));
	}

	public Review findReviewById(Integer id) {
		return throwReviewNotFoundException(id);
	}

	public List<Review> loadReviewsByFilmId(Integer filmId, Integer count) {
//		throwEntityNotFoundByFilmId(filmId);

		List<Review> reviews = (filmId < 0)
				? repository.load(count)
				: repository.load(filmId, count);
		return reviews.isEmpty() ? Collections.emptyList() : reviews;
	}

	public Review updateReview(Review review) { //TODO в разработке
		throwUserNotFoundException(review.getUserId());
		throwFilmNotFoundException(review.getFilmId());

		return repository.update(review);
	}

	public Review removeReviewById(Integer id) { //TODO в разработке
		return repository.remove(id);
	}

	public Review userLikesReview(Integer id, Integer userId) {
		throwReviewNotFoundException(id);
		throwUserNotFoundException(userId);

		return repository.like(id, userId);
	}

	public Review dislikeToReview(Integer id, Integer userId) {
		throwReviewNotFoundException(id);
		throwUserNotFoundException(userId);

		return repository.dislike(id, userId);
	}

	public Review removeLike(Integer id, Integer userId) {
		throwReviewNotFoundException(id);
		throwUserNotFoundException(userId);

		return repository.removeLike(id, userId);
	}

	public Review removeDislike(Integer id, Integer userId) {
		throwReviewNotFoundException(id);
		throwUserNotFoundException(userId);

		return repository.removeDislike(id, userId);
	}

	private void throwUserNotFoundException(Integer userId) {
		try {
			userStorage.getById(userId);
		} catch (Exception e) {
			throw new NotFoundException(String.format("User by id '%d' not found", userId));
		}
	}

	private void throwFilmNotFoundException(Integer filmId) {
		try {
			filmStorage.getById(filmId);
		} catch (Exception e) {
			throw new NotFoundException(String.format("Film by id '%d' not found", filmId));
		}
	}

	private Review throwReviewNotFoundException(Integer reviewId) {
		Review byId = repository.findById(reviewId);
		if (byId == null) {
			throw new NotFoundException(String.format("Review by id '%d' not found", reviewId));
		}
		return byId;
	}
}

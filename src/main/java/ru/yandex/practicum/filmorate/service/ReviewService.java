package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewRepository;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository<Integer, Review> repository;
	private final FilmStorage filmStorage;
	private final UserStorage userStorage;

	public Review addReview(Review review) {
		throwEntityNotFoundByUserId(review.getUserId());
		throwEntityNotFoundByFilmId(review.getFilmId());

		return review.getUseful() == null
				? repository.unload(review.withUseful(0))
				: repository.unload(review);
	}

	public Review findReviewById(Integer id) {
		return repository.findById(id);
	}

	public List<Review> loadReviewsByFilmId(Integer filmId, Integer count) {
		throwEntityNotFoundByFilmId(filmId);

		return (filmId < 0)
				? repository.load(count)
				: repository.load(filmId, count);
	}

	public Review updateReview(Review review) { //TODO в разработке
		throwEntityNotFoundByUserId(review.getUserId());
		throwEntityNotFoundByFilmId(review.getFilmId());

		return repository.update(review);
	}

	public Review removeReviewById(Integer id) { //TODO в разработке
		return repository.remove(id);
	}

	public Review likeToReview(Integer id, Integer userId) {
		return null;
	}

	public Review dislikeToReview(Integer id, Integer userId) {
		return null;
	}

	public Review removeLike(Integer id, Integer userId) {
		return null;
	}

	public Review removeDislike(Integer id, Integer userId) {
		return null;
	}

	private void throwEntityNotFoundByUserId(Integer userId) {
		if (userStorage.getById(userId) == null) {
			throw new NotFoundException("User not found by id " + userId);
		}
	}

	private void throwEntityNotFoundByFilmId(Integer filmId) {
		if (filmStorage.getById(filmId) == null) {
			throw new NotFoundException("Film not found by id " + filmId);
		}
	}
}

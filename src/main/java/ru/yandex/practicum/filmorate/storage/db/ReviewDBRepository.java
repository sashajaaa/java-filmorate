package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.BuildException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.sql_query.ReviewQuery;
import ru.yandex.practicum.filmorate.storage.interfaces.InteractionReviewRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDBRepository implements InteractionReviewRepository<Integer, Review> {

	private final JdbcTemplate jdbcTemplate;
	private final ReviewQuery reviewQuery;

	@Override
	public Optional<Review> unload(Review review) {
		Integer isCheck = jdbcTemplate.query(reviewQuery.getSelectUniqIdFromReviews(),
				ps -> ps.setInt(1, review.getUserId()),
				rs -> {
					if (rs.next()) {
						int filmId = rs.getInt("film_id");
						if (filmId == review.getFilmId()) {
							return 1;
						}
					}
					return null;
				});
		return isCheck == null ? Optional.of(findById(saveToDB(review))) : Optional.empty();
	}

	private Integer saveToDB(Review review) {
		log.info("SAVE -- {}", review);
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(
					reviewQuery.getInsertIntoReview(),
					new String[]{"review_id"});
			ps.setString(1, review.getContent());
			ps.setBoolean(2, review.getIsPositive());
			ps.setInt(3, review.getUserId());
			ps.setInt(4, review.getFilmId());
			ps.setInt(5, review.getUseful());
			return ps;
		}, keyHolder);

		int id = requireNonNull(keyHolder.getKey()).intValue();

		Review reviewId = review.withReviewId(id);
		log.info("Review with ID - {}", reviewId);

		insertToUniqIdReview(reviewId);
		insertToUniqFilmReview(reviewId);
		return id;
	}

	public Review findById(Integer id) {
		return jdbcTemplate.query(reviewQuery.getSelectReviewById(),
				ps -> ps.setInt(1, id),
				rs -> rs.next() ? collectReview(rs) : null);
	}

	private Review collectReview(ResultSet rs) {
		Review review;
		try {
			review = Review.builder()
					.reviewId(rs.getInt("review_id"))
					.content(rs.getString("content"))
					.isPositive(rs.getBoolean("is_positive"))
					.userId(rs.getInt("user_id"))
					.filmId(rs.getInt("film_id"))
					.useful(rs.getInt("useful"))
					.build();
		} catch (Exception e) {
			throw new BuildException("An error occurred while assembling the review");
		}
		return review;
	}

	private void insertToUniqIdReview(Review review) {
		jdbcTemplate.update(reviewQuery.getInsertUniqIdForReviews(),
				review.getUserId(),
				review.getFilmId());
	}

	private void insertToUniqFilmReview(Review review) {
		jdbcTemplate.update(reviewQuery.getInsertUniqFilmReviews(),
				review.getReviewId(),
				review.getFilmId());
	}

	@Override
	public Review update(Review review) {
		jdbcTemplate.update(reviewQuery.getUpdateReview(),
				ps -> {
					ps.setString(1, review.getContent());
					ps.setBoolean(2, review.getIsPositive());
					ps.setInt(3, review.getUseful());
					ps.setInt(4, review.getReviewId());
				});
		return findById(review.getReviewId());
	}

	@Override
	public Review remove(Integer id) {
		Review review = findById(id);

		if (review != null) {
			jdbcTemplate.update(reviewQuery.getRemoveReview(), id);
		}
		return review;
	}

	@Override
	public List<Review> load(Integer filmId, Integer count) {
		Map<Integer, Review> reviews = new LinkedHashMap<>();

		jdbcTemplate.query(reviewQuery.getSelectReviewByFilm(),
				ps -> {
					ps.setInt(1, filmId);
					ps.setInt(2, count);
				},
				rs -> {
					int id = rs.getInt("review_id");
					reviews.computeIfAbsent(id, m -> collectReview(rs));
				});
		return new ArrayList<>(reviews.values());
	}

	@Override
	public List<Review> load(Integer count) {
		Map<Integer, Review> reviews = new LinkedHashMap<>();

		jdbcTemplate.query(reviewQuery.getSelectReviewCount(),
				ps -> ps.setInt(1, count),
				rs -> {
						int id = rs.getInt("review_id");
						reviews.computeIfAbsent(id, m -> collectReview(rs));
				});
		return new ArrayList<>(reviews.values());
	}

	@Override
	public Review like(Integer to, Integer from) {
		jdbcTemplate.update(reviewQuery.getLike(), to, from);
		return positiveDeterminant(to);
	}

	@Override
	public Review dislike(Integer to, Integer from) {
		jdbcTemplate.update(reviewQuery.getDislike(), to, from);
		return positiveDeterminant(to);
	}

	@Override
	public Review removeLike(Integer to, Integer from) {
		jdbcTemplate.update(reviewQuery.getRemoveLike(), to, from);
		return positiveDeterminant(to);
	}

	@Override
	public Review removeDislike(Integer to, Integer from) {
		jdbcTemplate.update(reviewQuery.getRemoveDislike(), to, from);
		return positiveDeterminant(to);
	}

	private Review positiveDeterminant(Integer reviewId) {
		Integer likes = jdbcTemplate.query(reviewQuery.getCountLikes(),
				ps -> ps.setInt(1, reviewId),
				rs -> rs.next() ? rs.getInt("likes_count") : null);

		Integer dislikes = jdbcTemplate.query(reviewQuery.getCountDislikes(),
				ps -> ps.setInt(1, reviewId),
				rs -> rs.next() ? rs.getInt("dislikes_count") : null);

		int result = (likes != null && dislikes != null) ? likes - dislikes : 0;
		Review review = findById(reviewId);
		return update(review.withUseful(result).withIsPositive(result > 0));
	}
}

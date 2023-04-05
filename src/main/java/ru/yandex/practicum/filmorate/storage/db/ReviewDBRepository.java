package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.sql_query.ReviewQuery;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDBRepository implements ReviewRepository<Integer, Review> {

	private final JdbcTemplate jdbcTemplate;
	private final ReviewQuery reviewQuery;

	@Override
	public Review unload(Review review) {
		Integer isCheck = jdbcTemplate.query(reviewQuery.getSelectUniqIdFromReviews(),
				ps -> ps.setInt(1, review.getUserId()),
				rs -> {
					if (rs.next()) {
						int filmId = rs.getInt("film_id");
						log.info("Film id from table is {}", filmId);
						if (filmId == review.getFilmId()) {
							return 1;
						}
					}
					return null;
				});

		return isCheck == null ? findById(saveToDB(review)) : null;
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
		return id;
	}

	public Review findById(Integer id) {
		return jdbcTemplate.query(reviewQuery.getSelectReviewById(),
				ps -> ps.setInt(1, id),
				rs -> {
					Review review = null;

					while (rs.next()) {
						if (review == null) {
							review = collectReview(rs);
						}
					}
					return review;
				});
	}

	private Review collectReview(ResultSet rs) throws SQLException {
		return Review.builder()
				.reviewId(rs.getInt("review_id"))
				.content(rs.getString("content"))
				.isPositive(rs.getBoolean("is_positive"))
				.userId(rs.getInt("user_id"))
				.filmId(rs.getInt("film_id"))
				.useful(rs.getInt("useful"))
				.build();
	}

	private void insertToUniqIdReview(Review review) {
		jdbcTemplate.update(reviewQuery.getInsertUniqIdForReviews(), review.getUserId(), review.getFilmId());
	}

	public Review update(Review review) {
		jdbcTemplate.update(reviewQuery.getUpdateReview(),
				ps -> {
					ps.setString(1, review.getContent());
					ps.setBoolean(2, review.getIsPositive());
					ps.setInt(3, review.getReviewId());
				});
		return findById(review.getReviewId());
	}

	public Review remove(Integer id) {
		Review review = findById(id);

		if (review != null) {
			jdbcTemplate.update(reviewQuery.getRemoveReview(), id);
			jdbcTemplate.update(reviewQuery.getRemoveRelationshipReview(), ps -> {
				ps.setInt(1, review.getUserId());
				ps.setInt(2, review.getFilmId());
			});
			jdbcTemplate.update(reviewQuery.getRemoveRelationshipWithUser(), ps -> {
				ps.setInt(1, review.getReviewId());
				ps.setInt(2, review.getUserId());
			});
		}
		return review;
	}

	public List<Review> load(Integer filmId, Integer count) { // TODO в разработке (не задано количество)
		Map<Integer, Review> reviews = new HashMap<>();
		jdbcTemplate.query(reviewQuery.getSelectReviewByFilm(),
				ps -> {
					ps.setInt(1, filmId);
					ps.setInt(2, count);
				},
				rs -> {
					while (rs.next()) {
						int id = rs.getInt("review_id");
						Review review = reviews.get(id);

						if (review == null) {
							review = collectReview(rs);
						}
						reviews.put(id, review);
					}
				});

		return new ArrayList<>(reviews.values());
	}

	public List<Review> load(Integer count) {
		Map<Integer, Review> reviews = new HashMap<>();
		jdbcTemplate.query(reviewQuery.getSelectReviewCount(), ps -> ps.setInt(1, count),
				rs -> {
					while (rs.next()) {
						int id = rs.getInt("review_id");
						Review review = reviews.get(id);

						if (review == null) {
							review = collectReview(rs);
						}
						reviews.put(id, review);
					}
				});
		return new ArrayList<>(reviews.values());
	}
}

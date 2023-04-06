package ru.yandex.practicum.filmorate.sql_query;

import lombok.Value;
import org.springframework.stereotype.Component;

@Component
@Value
public class ReviewQuery {
	String selectReviewById = "SELECT * FROM reviews WHERE review_id = ?";
	String selectReview = "SELECT * FROM reviews";
	String selectUniqIdFromReviews = "SELECT film_id FROM un_reviews_user_film WHERE user_id = ?";
	String selectReviewByFilm = "SELECT r.* FROM reviews r JOIN un_review_film urf ON urf.review_id =r.review_id WHERE urf.film_id =? ORDER BY r.useful DESC LIMIT ?";
	String selectReviewCount = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";

	String updateReview = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
	String insertIntoReview = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ? ,? ,?)";
	String insertUniqIdForReviews = "INSERT INTO un_reviews_user_film (user_id, film_id) VALUES (?, ?)";
	String removeReview = "DELETE FROM reviews WHERE review_id = ?";
	String removeRelationshipReview = "DELETE FROM un_reviews_user_film WHERE user_id = ? AND film_id = ?";
	String removeRelationshipWithUser = "DELETE FROM un_review_user WHERE review_id = ? AND user_id = ?";
	//	private String selectReviewByFilmId = "SELECT * FROM un_review_film WHERE film_id = ?";
}

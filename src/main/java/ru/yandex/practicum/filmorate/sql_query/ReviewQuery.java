package ru.yandex.practicum.filmorate.sql_query;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public final class ReviewQuery {
	@Value("${review.select-review-by-id}")
	private String selectReviewById;

	@Value("${review.select-reviews}")
	private String selectReviews;

	@Value("${review.select-uniq-id-from-reviews}")
	private String selectUniqIdFromReviews;

	@Value("${review.select-review-by-film}")
	private String selectReviewByFilm;

	@Value("${review.select-review-count}")
	private String selectReviewCount;

	@Value("${review.update-review}")
	private String updateReview;

	@Value("${review.update-review-useful}")
	private String updateReviewUseful;

	@Value("${review.insert-nto-review}")
	private String insertIntoReview;

	@Value("${review.insert-uniq-id-for-reviews}")
	private String insertUniqIdForReviews;

	@Value("${review.insert-uniq-film-reviews}")
	private String insertUniqFilmReviews;

	@Value("${review.remove-review}")
	private String removeReview;

	@Value("${review.remove-relationship-review}")
	private String removeRelationshipReview; //not use
	@Value("${review.remove-relationship-with-user}")
	private String removeRelationshipWithUser; //not use
	@Value("${review.remove-film-review}")
	private String removeFilmReview; //not use

	@Value("${review.insert-like}")
	private String insertLike;

	@Value("${review.insert-dislike}")
	private String insertDislike;

	@Value("${review.remove-like}")
	private String removeLike;

	@Value("${review.remove-dislike}")
	private String removeDislike;

	@Value("${review.count-likes}")
	private String countLikes;

	@Value("${review.count-dislikes}")
	private String countDislikes;
}

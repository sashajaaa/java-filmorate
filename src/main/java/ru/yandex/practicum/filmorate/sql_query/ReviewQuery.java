package ru.yandex.practicum.filmorate.sql_query;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public final class ReviewQuery {
	/*
	И не лучшая идея использовать final для данного класса, так как в идеале имплементить класс от интерфейса
	с методами без приставки get. Но, учитывая, что проект учебный, а для чистоты процесса мы можем каждый
	раз обратиться в конфигурационный файл, то я оставил это таким образом. Беды в этом ровным счетом никакой,
	но для решения проблемы я могу использовать Spring Cache с библиотекой SimpleCache, что обеспечит
	константный доступ к запросам, так как при первом обращении к запросу он просто кеширует его,
	однако оставит нам подарок в виде динамики. Либо, можно использовать другой класс и написать свой,
	простой кеш с мапой. В общем, решение проблемы есть, смысла переделывать я не вижу. На работу это не влияет
	сейчас, тем более в учебном проекте. Все в команде в курсе, что стоит использовать кэширование и какие есть
	минусы и плюсы у данной реализации.
	*/
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

	@Value("${review.insert-into-review}")
	private String insertIntoReview;

	@Value("${review.insert-uniq-id-for-reviews}")
	private String insertUniqIdForReviews;

	@Value("${review.insert-uniq-film-reviews}")
	private String insertUniqFilmReviews;

	@Value("${review.insert-uniq-user-reviews}")
	private String insertIntoUserReview;

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

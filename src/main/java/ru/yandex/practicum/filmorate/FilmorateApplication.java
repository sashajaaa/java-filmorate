package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
    public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}
}

/*DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS un_review_film;
DROP TABLE IF EXISTS un_review_user;
DROP TABLE IF EXISTS un_reviews_user_film;
DROP TABLE IF EXISTS un_user_likes_review;
DROP TABLE IF EXISTS un_user_dislikes_review;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films_directors;
DROP TABLE IF EXISTS directors;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS rating_mpa;*/

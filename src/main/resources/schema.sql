DROP TABLE IF EXISTS film_genres;
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
DROP TABLE IF EXISTS rating_mpa;

CREATE TABLE IF NOT EXISTS directors
(
    director_id   INT          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id   INT          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS rating_mpa
(
    rating_id   INT          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      INT          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name    VARCHAR(255) NOT NULL,
    description  VARCHAR(200) NOT NULL,
    duration     INT          NOT NULL,
    release_date TIMESTAMP    NOT NULL,
    rating_id    INT REFERENCES rating_mpa (rating_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    user_id   INT          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_name VARCHAR(255),
    login     VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    birthday  DATE
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  INT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE,
    genre_id INT NOT NULL REFERENCES genres (genre_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id INT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE,
    user_id INT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   INT     NOT NULL REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    friend_id INT     NOT NULL REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    status    BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS films_directors
(
    film_id     INT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE,
    director_id INT NOT NULL REFERENCES directors (director_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE reviews
(
    review_id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    content     VARCHAR(255) NOT NULL,
    is_positive BOOLEAN      NOT NULL,
    user_id     INTEGER      NOT NULL,
    film_id     INTEGER      NOT NULL,
    useful      INTEGER      NOT NULL
);

CREATE TABLE un_reviews_user_film
(
    user_id INTEGER,
    film_id INTEGER,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE un_review_user
(
    review_id INTEGER,
    user_id   INTEGER,
    PRIMARY KEY (review_id, user_id),
    FOREIGN KEY (review_id) REFERENCES reviews (review_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE un_review_film
(
    review_id INTEGER,
    film_id   INTEGER,
    PRIMARY KEY (review_id, film_id),
    FOREIGN KEY (review_id) REFERENCES reviews (review_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE un_user_likes_review
(
    review_id INTEGER,
    user_id   INTEGER,
    PRIMARY KEY (review_id, user_id),
    FOREIGN KEY (review_id) REFERENCES reviews (review_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE un_user_dislikes_review
(
    review_id INTEGER,
    user_id   INTEGER,
    PRIMARY KEY (review_id, user_id),
    FOREIGN KEY (review_id) REFERENCES reviews (review_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);
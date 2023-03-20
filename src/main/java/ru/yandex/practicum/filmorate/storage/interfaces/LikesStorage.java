package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getSortedFilms();

    void likeCheck(Integer filmId, Integer userId);
}
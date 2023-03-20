package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmStorage extends Storage<Film>, LikesStorage {
    void addGenre(int filmId, Set<Genre> genres);

    Set<Genre> getGenres(int filmId);
}
package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage extends LikesStorage {
    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    Film delete(int id);

    Film getById(int id);

    void addGenre(int filmId, Set<Genre> genres);

    List<Film> getDirectorsFilms(int directorId, String sortBy);

    List<Film> search(String lookFor, int choose);

    boolean containsFilm(int id);
}
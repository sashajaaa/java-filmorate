package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.SearchType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage extends LikesStorage {
    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    Film delete(Integer id);

    Film getById(Integer id);

    void addGenre(int filmId, Set<Genre> genres);

    List<Film> getDirectorsFilms(int directorId, String sortBy);

    List<Film> search(String lookFor, SearchType searchType);

    boolean containsFilm(int id);
}
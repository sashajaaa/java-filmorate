package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate LIMIT_DATE = LocalDate.from(LocalDateTime.of(1895, 12, 28, 0, 0));
    private static final int LIMIT_LENGTH_OF_DESCRIPTION = 200;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, DirectorStorage directorStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
    }

    public List<Film> getDirectorsFilmsSortedBy(int directorId, String sortBy) {
        containsDirector(directorId);
        return filmStorage.getDirectorsFilms(directorId, sortBy);
    }

    public Collection<Film> getAll() {
        log.info("List of all movies: " + filmStorage.getAll().size());
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        validate(film, "Movie form is filled in incorrectly");
        Film result = filmStorage.create(film);
        log.info("Movie successfully added: " + film);
        return result;
    }

    public Film update(Film film) {
        containsFilm(film.getId());
        validate(film, "Movie update form is filled in incorrectly");
        Film result = filmStorage.update(film);
        log.info("Movie successfully updated: " + film);
        return result;
    }

    public Film delete(int filmId) {
        containsFilm(filmId);
        return filmStorage.delete(filmId);
    }

    public Film getById(int id) {
        containsFilm(id);
        log.info("Requested user with ID = " + id);
        return filmStorage.getById(id);
    }

    public void addLike(int filmId, int userId) {
        containsFilm(filmId);
        containsUser(userId);
        filmStorage.addLike(filmId, userId);
        log.info("Like successfully added");
    }

    public void removeLike(int filmId, int userId) {
        containsFilm(filmId);
        containsUser(userId);
        filmStorage.removeLike(filmId, userId);
        log.info("Like successfully removed");
    }

    public List<Film> getPopular(int count) {
        List<Film> result = new ArrayList<>(filmStorage.getPopular(count));
        log.info("Requested a list of popular movies");
        return result;
    }

    public List<Film> search(String lookFor, String choose) {
        int chooseId;
        if (choose.contains("director")) {
            if (choose.contains("title")) {
                chooseId = 3;
            } else {
                chooseId = 2;
            }
        } else {
            chooseId = 1;
        }
        return filmStorage.search(lookFor, chooseId);
    }

    public void containsFilm(int id) {
        if (!filmStorage.containsFilm(id)) {
            throw new NotFoundException("Movie with ID = " + id + " not found");
        }
    }

    private void containsUser(int id) {
        if (!userStorage.containsUser(id)) {
            throw new NotFoundException("User with id=" + id + " not exist. ");
        }
    }

    private void containsDirector(int id) {
        if (!directorStorage.containsDirector(id)) {
            throw new NotFoundException("Director with id=" + id + " not exist. ");
        }
    }

    protected void validate(Film film, String message) {
        if (film.getDescription().length() > LIMIT_LENGTH_OF_DESCRIPTION || film.getReleaseDate().isBefore(LIMIT_DATE)) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }
}
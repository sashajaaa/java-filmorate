package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage storage;
    private int id = 1;
    private static final LocalDate LIMIT_DATE = LocalDate.from(LocalDateTime.of(1895, 12, 28, 0, 0));
    private static final int LIMIT_LENGTH_OF_DESCRIPTION = 200;

    @Autowired
    public FilmService(@Qualifier("dataBase") FilmStorage filmStorage, @Qualifier("dataBase") UserStorage userStorage) {
        this.userStorage = userStorage;
        this.storage = filmStorage;
    }

    public Collection<Film> getAll() {
        log.info("List of all movies: " + storage.getEntities().size());
        return storage.getAll();
    }

    public Film create(Film film) {
        validate(film, "Movie form is filled in incorrectly");
        film.setId(id++);
        Film result = storage.create(film);
        log.info("Movie successfully added: " + film);
        return result;
    }

    public Film update(Film film) {
        validate(film, "Movie update form is filled in incorrectly");
        Film result = storage.update(film);
        log.info("Movie successfully updated: " + film);
        return result;
    }

    public Film delete(int filmId) {
        log.info("Deleted film with id: {}", filmId);
        return storage.delete(filmId);
    }

    public Film getById(Integer id) {
        log.info("Requested user with ID = " + id);
        return storage.getById(id);
    }


    public void addGenres(Film film) {
        storage.addGenre(film.getId(), film.getGenres());
    }

    public void addLike(Integer filmId, Integer userId) {
        likeCheck(filmId, userId);
        storage.addLike(filmId, userId);
        log.info("Like successfully added");
    }

    public void removeLike(Integer filmId, Integer userId) {
        likeCheck(filmId, userId);
        storage.removeLike(filmId, userId);
        log.info("Like successfully removed");
    }

    private void likeCheck(Integer filmId, Integer userId) {
        storage.likeCheck(filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        List<Film> result = getSortedFilms().stream().limit(count).collect(Collectors.toList());
        log.info("Requested a list of popular movies");
        return result;
    }

    public List<Film> getSortedFilms() {
        return storage.getSortedFilms();
    }

    protected void validate(Film film, String message) {
        if (film.getDescription().length() > LIMIT_LENGTH_OF_DESCRIPTION || film.getReleaseDate().isBefore(LIMIT_DATE)) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private int id = 1;
    private static final LocalDate LIMIT_DATE = LocalDate.from(LocalDateTime.of(1895, 12, 28, 0, 0));
    private static final int LIMIT_LENGTH_OF_DESCRIPTION = 200;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAll() {
        log.info("List of all movies: " + filmStorage.getAll().size());
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        validate(film, "Movie form is filled in incorrectly");
        film.setId(id++);
        Film result = filmStorage.create(film);
        log.info("Movie successfully added: " + film);
        return result;
    }

    public Film update(Film film) {
        validate(film, "Movie update form is filled in incorrectly");
        Film result = filmStorage.update(film);
        log.info("Movie successfully updated: " + film);
        return result;
    }

    public void delete(int filmId) {
        if (getById(filmId) == null) {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
        log.info("Deleted film with id: {}", filmId);
        filmStorage.delete(filmId);
    }

    public Film getById(Integer id) {
        log.info("Requested user with ID = " + id);
        return filmStorage.getById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        likeCheck(filmId, userId);
        filmStorage.addLike(filmId, userId);
        log.info("Like successfully added");
    }

    public void removeLike(Integer filmId, Integer userId) {
        likeCheck(filmId, userId);
        filmStorage.removeLike(filmId, userId);
        log.info("Like successfully removed");
    }

    private void likeCheck(Integer filmId, Integer userId) {
        filmStorage.likeCheck(filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        List<Film> result = getSortedFilms().stream().limit(count).collect(Collectors.toList());
        log.info("Requested a list of popular movies");
        return result;
    }

    public List<Film> getSortedFilms() {
        return filmStorage.getSortedFilms();
    }

    protected void validate(Film film, String message) {
        if (film.getDescription().length() > LIMIT_LENGTH_OF_DESCRIPTION || film.getReleaseDate().isBefore(LIMIT_DATE)) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }
}
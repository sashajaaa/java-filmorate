package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate LIMIT_DATE = LocalDate.from(
            LocalDateTime.of(1895, 12, 28, 0, 0));
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
        log.info("Director`s {} films list sorted by request {}", directorId, sortBy);
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

    public Film delete(Integer filmId) {
        containsFilm(filmId);
        log.info("Request to delete the movie by ID = " + filmId + " received");
        log.info("Deleted film with id: {}", filmId);
        return filmStorage.delete(filmId);
    }

    public Film getById(Integer id) {
        containsFilm(id);
        log.info("Requested user with ID = " + id);
        return filmStorage.getById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        containsFilm(filmId);
        containsUser(userId);
        filmStorage.addLike(filmId, userId);
        User user = userStorage.getById(userId);
        Set<Integer> userLikes = user.getLikes();
        if (userLikes == null) {
            userLikes = new HashSet<>(filmId);
        }
        userLikes.add(filmId);
        user.setLikes(userLikes);
        userStorage.update(user);
        log.info("Like from user with id=" + userId + " to film with id=" + filmId + " successfully added");
    }

    public void removeLike(Integer filmId, Integer userId) {
        containsFilm(filmId);
        containsUser(userId);
        filmStorage.removeLike(filmId, userId);
        log.info("Like successfully removed");
    }

    public List<Film> getPopular(Integer count) {
        List<Film> result = new ArrayList<>(filmStorage.getPopular(count));
        log.info("Requested a list of popular movies");
        return result;
    }

    public List<Film> search(String lookFor, String choose) {
        log.info("Request for get films by substring {}", lookFor);
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
        if (lookFor.isEmpty()) {
            chooseId = 0;
        }
        return filmStorage.search(lookFor, chooseId);
    }

    private void containsFilm(int id) {
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

    private void validate(Film film, String message) {
        if (film.getDescription().length() > LIMIT_LENGTH_OF_DESCRIPTION || film.getReleaseDate()
                .isBefore(LIMIT_DATE)) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }
}
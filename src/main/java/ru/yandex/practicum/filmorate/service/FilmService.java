package ru.yandex.practicum.filmorate.service;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
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

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private static final LocalDate LIMIT_DATE = LocalDate.from(
            LocalDateTime.of(1895, 12, 28, 0, 0));
    private static final int LIMIT_LENGTH_OF_DESCRIPTION = 200;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<Film> getDirectorsFilmsSortedBy(int directorId, String sortBy) {
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
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("User with ID = " + userId + " not found");
        }
        filmStorage.addLike(filmId, userId);
        User user = userStorage.getById(userId);
        Set<Integer> userLikes = user.getLikes();
        if (userLikes == null) {
            userLikes = new HashSet<>(filmId);
        }
        userLikes.add(filmId);
        user.setLikes(userLikes);
        userStorage.update(user);
        log.info("Like successfully added");
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        if (film != null) {
            if (userStorage.getById(userId) != null) {
                filmStorage.removeLike(filmId, userId);
                log.info("Like successfully removed");
            } else {
                throw new NotFoundException("User with ID = " + userId + " not found");
            }
        } else {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
    }

    public List<Film> getPopular(Integer count, Integer genreId, Integer year) {
        List<Film> result = new ArrayList<>(filmStorage.getPopular(count, genreId, year));
        log.info("Requested a list of popular movies");
        return result;
    }

    protected void validate(Film film, String message) {
        if (film.getDescription().length() > LIMIT_LENGTH_OF_DESCRIPTION || film.getReleaseDate()
                .isBefore(LIMIT_DATE)) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }

    public List<Film> getCommonMovies(Integer userId, Integer friendId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("User with ID = " + userId + " not found");
        }
        if (userStorage.getById(friendId) == null) {
            throw new NotFoundException("User with ID = " + friendId + " not found");
        }
        Set<Integer> userLikes = userStorage.getById(userId).getLikes();
        Set<Integer> friendLikes = userStorage.getById(friendId).getLikes();
        List<Film> commonMovies = new ArrayList<>();
        for (Integer filmId : userLikes) {
            if (friendLikes.contains(filmId)) {
                commonMovies.add(filmStorage.getById(filmId));
            }
        }
        if (commonMovies.size() > 1) {
            commonMovies.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());
        }
        return commonMovies;
    }
}
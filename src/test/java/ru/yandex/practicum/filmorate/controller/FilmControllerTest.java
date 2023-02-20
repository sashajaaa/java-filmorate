package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    public void start() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage,userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    void createUnlimitReleasedFilm_shouldShowErrorMessage() {
        Set<Integer> likes = Set.of(1, 2, 3);
        Film film = Film.builder()
                .id(1)
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(200))
                .duration(180)
                .likes(likes)
                .build();
        ValidationException e = assertThrows(ValidationException.class, () -> filmController.create(film));

        assertEquals("The object form is filled in incorrectly", e.getMessage());
    }

    @Test
    void updateUnlimitReleasedFilm_shouldShowErrorMessage() {
        Set<Integer> likes = Set.of(1, 2, 3);
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(43))
                .duration(240)
                .likes(likes)
                .build();
        filmController.create(film);
        Film filmUpdate = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(230))
                .duration(193)
                .build();

        ValidationException e = assertThrows(ValidationException.class, () -> filmController.update(filmUpdate));

        assertEquals("Object update form was filled out incorrectly", e.getMessage());
    }
}
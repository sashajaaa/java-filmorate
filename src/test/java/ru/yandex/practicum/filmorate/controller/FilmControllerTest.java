package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

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
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(200))
                .duration(180)
                .build();
        ValidationException e = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() {
                filmController.create(film);
            }
        });

        assertEquals("The object form is filled in incorrectly", e.getMessage());
    }

    @Test
    void updateUnlimitReleasedFilm_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(43))
                .duration(240)
                .build();
        filmController.create(film);
        Film filmUpdate = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(230))
                .duration(193)
                .build();

        ValidationException e = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() {
                filmController.update(filmUpdate);
            }
        });

        assertEquals("Object update form was filled out incorrectly", e.getMessage());
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    public void start() {
        filmController = new FilmController();
    }

    @Test
    void createUnlimitReleasedFilm_shouldShowErrorMessage() {
        Film film = new Film(192, "Movie", "Interesting", LocalDate.now().minusYears(200), 180);

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
        Film film = new Film(192, "Movie", "Interesting", LocalDate.now().minusYears(43), 240);
        filmController.create(film);
        Film filmUpdate = new Film(192, "Movie", "Interesting", LocalDate.now().minusYears(230), 193);

        ValidationException e = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() {
                filmController.update(filmUpdate);
            }
        });

        assertEquals("Object update form was filled out incorrectly", e.getMessage());
    }
}
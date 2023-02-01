package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends Controller<Film> {
    private static final LocalDate DATE_LIMIT = LocalDate.from(LocalDateTime.of(1895, 12, 28, 0, 0));
    private static final int DESCRIPTION_LENGTH_LIMIT = 200;

    @GetMapping
    @Override
    public Collection<Film> findAll() {
        log.info("Current number of films: " + entities.size());
        return entities.values();
    }

    @PostMapping
    @Override
    public Film create(@Valid @RequestBody Film film) {
        super.create(film);
        log.info("Film successfully added: " + film);
        return film;
    }

    @PutMapping
    @Override
    public Film update(@Valid @RequestBody Film film) {
        super.update(film);
        return film;
    }

    @Override
    public boolean validation(Film film) {
        return !(film.getDescription().length() > DESCRIPTION_LENGTH_LIMIT ||
                film.getReleaseDate().isBefore(DATE_LIMIT));
    }
}
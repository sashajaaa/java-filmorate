package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final FeedService feedService;

    @Autowired
    public FilmController(FilmService filmService, FeedService feedService) {
        this.filmService = filmService;
        this.feedService = feedService;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorsFilmsSortedBy(@PathVariable int directorId,
                                                @RequestParam(defaultValue = "likes") String sortBy) {
        return filmService.getDirectorsFilmsSortedBy(directorId, sortBy);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @DeleteMapping("/{filmId}")
    public void deleteById(@PathVariable Integer filmId) {
        filmService.delete(filmId);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
        feedService.addFeed(Long.valueOf(userId), EventType.LIKE, OperationType.ADD, Long.valueOf(id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
        feedService.addFeed(Long.valueOf(userId), EventType.LIKE, OperationType.REMOVE, Long.valueOf(id));
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Integer count,
                                 @RequestParam(required = false) Integer genreId,
                                 @RequestParam(required = false) Integer year) {
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonMovies(@RequestParam Integer userId,
                                      @RequestParam Integer friendId) {
        return filmService.getCommonMovies(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query,
                             @RequestParam(defaultValue = "title") String by) {
        return filmService.search(query, by);
    }
}
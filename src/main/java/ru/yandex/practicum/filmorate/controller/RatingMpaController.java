package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingMpaController {
    private final RatingMpaService ratingMpaService;

    public RatingMpaController(RatingMpaService ratingMpaService) {
        this.ratingMpaService = ratingMpaService;
    }

    @GetMapping
    public List<RatingMpa> getRatingsMpa() {
        return ratingMpaService.getRatingsMpa();
    }

    @GetMapping("/{id}")
    public RatingMpa getRatingMpaById(@PathVariable Integer id) {
        return ratingMpaService.getRatingMpaById(id);
    }
}
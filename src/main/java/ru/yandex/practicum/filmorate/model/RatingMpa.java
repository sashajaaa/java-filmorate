package ru.yandex.practicum.filmorate.model;

import org.springframework.stereotype.Service;

@Service
public class RatingMpa extends ListModel {
    public RatingMpa() {
    }

    public RatingMpa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.db.RatingMpaDbStorage;

import java.util.List;

@Service
@Slf4j
public class RatingMpaService {
    private final RatingMpaDbStorage ratingMpaDbStorage;

    @Autowired
    public RatingMpaService(RatingMpaDbStorage ratingMpaDbStorage) {
        this.ratingMpaDbStorage = ratingMpaDbStorage;
    }

    public RatingMpa getRatingMpaById(int id) {
        containsMPA(id);
        log.info("Request for MPA with id=" + id);
        return ratingMpaDbStorage.getRatingMpaById(id);
    }

    public List<RatingMpa> getRatingsMpa() {
        log.info("Request for MPAs");
        return ratingMpaDbStorage.getRatingsMpa();
    }

    private void containsMPA(int id) {
        if (!ratingMpaDbStorage.containsMpa(id)) {
            throw new NotFoundException("Rating not found");
        }
    }
}
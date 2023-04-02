package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(int id) {
        containsGenre(id);
        log.info("Request or genre with id=" + id);
         return genreDbStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        log.info("Request or genres");
        return genreDbStorage.getAllGenres();
    }

   private void containsGenre(int id) {
       if (!genreDbStorage.containsGenre(id)) {
           throw new NotFoundException("Genre with id=" + id + " not found");
       }
   }
}
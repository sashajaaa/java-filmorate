package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        containsDirector(director.getId());
        return directorStorage.updateDirector(director);
    }

    public Director deleteDirectorById(int id) {
        containsDirector(id);
        return directorStorage.deleteDirectorById(id);
    }

    public void deleteAllDirectors() {
        directorStorage.deleteAllDirectors();
    }

    public Director getDirectorById(Integer id) {
        containsDirector(id);
        return directorStorage.getDirectorById(id);
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public void containsDirector(int id) {
        if (!directorStorage.containsDirector(id)) {
            throw new NotFoundException("Director with id=" + id + " is absent");
        }
    }
}
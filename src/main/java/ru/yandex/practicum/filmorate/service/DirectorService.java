package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirecorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorService {

    private final DirecorStorage direcorStorage;

    @Autowired
    public DirectorService(DirecorStorage direcorStorage) {
        this.direcorStorage = direcorStorage;
    }

    public Director addDirector(Director director) {
        return direcorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return direcorStorage.updateDirector(director);
    }

    public Director deleteDirectorById(Integer id) {
        return direcorStorage.deleteDirectorById(id);
    }

    public void deleteAllDirectors() {
        direcorStorage.deleteAllDirectors();
    }

    public Director getDirectorById(Integer id) {
        return direcorStorage.getDirectorById(id);
    }

    public List<Director> getAllDirectors() {
        return direcorStorage.getAllDirectors();
    }


}

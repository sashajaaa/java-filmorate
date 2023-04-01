package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    public Director addDirector(Director director);

    public List<Director> getAllDirectors();

    public Director getDirectorById(Integer id);

    List<Director> getAllDirectors();

    Director getDirectorById(Integer id);

    Director updateDirector(Director director);

    boolean containsDirector(int id);
    Director deleteDirectorById(Integer id);

    void deleteAllDirectors();
}
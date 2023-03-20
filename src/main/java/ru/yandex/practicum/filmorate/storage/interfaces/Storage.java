package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Model;

import java.util.Collection;
import java.util.Map;

public interface Storage<T extends Model> {
    Collection<T> getAll();

    T create(T obj);

    T update(T obj);

    T delete(int id);

    T getById(Integer id);

    Map<Integer, T> getEntities();
}
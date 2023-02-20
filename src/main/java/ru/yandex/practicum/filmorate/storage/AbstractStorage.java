package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractStorage<T extends Model> implements Storage<T> {
    protected final Map<Integer, T> entities = new HashMap<>();

    @Override
    public Collection<T> getAll() {
        log.info("List of all objects: " + entities.size());
        return new ArrayList<>(entities.values());
    }

    @Override
    public T create(T obj) {
        entities.put(obj.getId(), obj);
        return obj;
    }

    @Override
    public T update(T obj) {
        if (!entities.containsKey(obj.getId())) {
            throw new ValidationException("Object is not in list");
        }
        entities.put(obj.getId(), obj);
        log.info("Object successfully updated: " + obj);
        return obj;
    }

    @Override
    public T delete(Integer id) {
        T obj;
        if (entities.containsKey(id)) {
            obj = entities.get(id);
        } else {
            throw new NotFoundException("Object is not in list");
        }
        return obj;
    }

    public T getById(Integer id) {
        T obj;
        if (entities.containsKey(id)) {
            obj = entities.get(id);
        } else {
            throw new NotFoundException("Object is not in list");
        }
        return obj;
    }
}
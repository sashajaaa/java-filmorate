package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class Controller<T extends Model> {
    protected final Map<Integer, T> entities = new HashMap<>();
    private int id = 0;

    public Collection<T> findAll() {
        log.info("List of all objects: " + entities.size());
        return entities.values();
    }

    public T create(T obj) {
        validate(obj, "The object form is filled in incorrectly");
        obj.setId(setId());
        entities.put(obj.getId(), obj);
        log.info("Object successfully added: " + obj);
        return obj;
    }

    public T update(T obj) {
        validate(obj, "Object update form was filled out incorrectly");
        if (entities.containsKey(obj.getId())) {
            entities.put(obj.getId(), obj);
            log.info("Object successfully updated: " + obj);
        } else {
            log.debug("Object is not in list");
            throw new ValidationException("Object is not in list");
        }
        return obj;
    }

    public int setId() {
        id += 1;
        return id;
    }

    public void validate(T obj, String message) {
        if (!validation(obj)) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }

    public boolean validation(T obj) {
        return false;
    }

    public Map<Integer, T> getEntities() {
        return entities;
    }
}
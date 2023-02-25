package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;

@Slf4j
@Service
public abstract class AbstractService<T extends Model> {
    protected final Storage<T> storage;

    protected AbstractService(Storage<T> storage) {
        this.storage = storage;
    }

    public Collection<T> getAll() {
        log.info("List of all objects: " + storage.getAll().size());
        return storage.getAll();
    }

    public T create(T obj) {
        validate(obj, "The object form is filled in incorrectly");
        preSave(obj);
        log.info("Object successfully added: " + obj);
        return storage.create(obj);
    }

    public T update(T obj) {
        validate(obj, "Object update form was filled out incorrectly");
        preSave(obj);
        log.info("Object successfully updated: " + obj);
        return storage.update(obj);
    }

    public T delete(Integer id) {
        log.info("Deleted object with id: {}", id);
        return storage.delete(id);
    }

    public T getById(Integer id) {
        log.info("Requested object with id: " + id);
        return storage.getById(id);
    }

    protected void validate(T obj, String message) {
        if (!doValidate(obj)) {
            throw new ValidationException(message);
        }
    }

    protected boolean doValidate(T obj) {
        return true;
    }

    protected void preSave(T obj) {
    }
}
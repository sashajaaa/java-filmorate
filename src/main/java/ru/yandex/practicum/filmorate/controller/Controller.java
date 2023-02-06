package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class Controller<T extends Model> {
    protected final Map<Integer, T> entities = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<T> findAll() {
        log.info("List of all objects: " + entities.size());
        return entities.values();
    }

    @PostMapping
    public T create(@Valid @RequestBody T obj) {
        validate(obj, "The object form is filled in incorrectly");
        obj.setId(setId());
        fixVoidName(obj);
        entities.put(obj.getId(), obj);
        log.info("Object successfully added: " + obj);
        return obj;
    }

    @PutMapping
    public T update(@Valid @RequestBody T obj) {
        validate(obj, "Object update form was filled out incorrectly");
        if (!entities.containsKey(obj.getId())) {
            throw new ValidationException("Object is not in list");
        } else {
            fixVoidName(obj);
            entities.put(obj.getId(), obj);
            log.info("Object successfully updated: " + obj);
        }
        return obj;
    }

    private int setId() {
        id += 1;
        return id;
    }

    private void validate(T obj, String message) {
        if (!doValidate(obj)) {
            throw new ValidationException(message);
        }
    }

    protected boolean doValidate(T obj) {
        return true;
    }

    public void fixVoidName(T obj){
    }
}
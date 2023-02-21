package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.service.AbstractService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
public abstract class Controller<T extends Model> {
    AbstractService service;

    @Autowired
    public Controller(AbstractService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<T> getAll() {
        log.info("List of all objects: " + service.getAll().size());
        return service.getAll();
    }

    @PostMapping
    public T create(@Valid @RequestBody T obj) {
        service.create(obj);
        log.info("Object successfully added: " + obj);
        return obj;
    }

    @PutMapping
    public T update(@Valid @RequestBody T obj) {
        service.update(obj);
        log.info("Object successfully updated: " + obj);
        return obj;
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable Integer id) {
        log.debug("Deleted object with id: ", id);
        service.delete(id);
    }
}
package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.storage.interfaces.Storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractStorage<T extends Model> implements Storage<T> {
    protected final Map<Integer, T> entities = new HashMap<>();

    @Override
    public Collection<T> getAll() {
        return entities.values();
    }

    @Override
    public T create(T obj) {
        entities.put(obj.getId(), obj);
        return obj;
    }

    @Override
    public T update(T obj) {
        if (entities.containsKey(obj.getId())) {
            entities.put(obj.getId(), obj);
        } else {
            throw new NotFoundException("Object is not in list");
        }
        return obj;
    }

    @Override
    public T delete(int id) {
        return entities.remove(id);
    }

    @Override
    public T getById(Integer id) {
        T obj;
        if (entities.containsKey(id)) {
            obj = entities.get(id);
        } else {
            throw new NotFoundException("Object is not in list");
        }
        return obj;
    }

    @Override
    public Map<Integer, T> getEntities() {
        return entities;
    }
}
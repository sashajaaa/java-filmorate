package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends FriendsStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User user);

    User delete(int id);

    User getById(int id);

    boolean containsUser(int id);
}
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService extends AbstractService<User> {
    @Autowired
    public UserService(UserStorage storage) {
        super(storage);
    }

    @Override
    protected void preSave(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (storage.getById(userId) == null || storage.getById(friendId) == null) {
            throw new NotFoundException("Object is not in list");
        }
        getById(userId).addFriend(friendId);
        getById(friendId).addFriend(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        if (storage.getById(userId) == null || storage.getById(friendId) == null) {
            throw new NotFoundException("Object is not in list");
        }
        getById(userId).deleteFriend(friendId);
        getById(friendId).deleteFriend(userId);
    }

    public List<User> getFriends(Integer userId) {
        if (storage.getById(userId) == null) {
            throw new NotFoundException("Object is not in list");
        }
        User user = getById(userId);
        return user.getFriends().stream()
                .map(storage::getById)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer user1Id, Integer user2Id) {
        if (storage.getById(user1Id) == null ||
                storage.getById(user2Id) == null) {
            throw new NotFoundException("Object is not in list");
        }
        List<User> userSet1 = getFriends(user1Id);
        List<User> userSet2 = getFriends(user2Id);
        return userSet1.stream().filter(userSet2::contains).
                sorted(Comparator.comparingInt(User::getId)).
                collect(Collectors.toList());
    }
}
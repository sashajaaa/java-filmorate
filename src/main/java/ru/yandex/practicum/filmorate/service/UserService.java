package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Component
@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    private final FilmStorage filmStorage;

    @Autowired
    public UserService(UserStorage storage, FilmStorage filmStorage) {
        this.storage = storage;
        this.filmStorage = filmStorage;
    }

    public Collection<User> getAll() {
        log.info("List of all users: " + storage.getAll().size());
        return storage.getAll();
    }

    public User create(User user) {
        validate(user, "User form is filled in incorrectly");
        preSave(user);
        User result = storage.create(user);
        log.info("User successfully added: " + user);
        return result;
    }

    public User update(User user) {
        validate(user, "User update form is filled in incorrectly");
        preSave(user);
        User result = storage.update(user);
        log.info("User successfully updated: " + user);
        return result;
    }

    public void delete(int userId) {
        if (getById(userId) == null) {
            throw new NotFoundException("User with ID = " + userId + " not found");
        }
        log.info("Deleted film with id: {}", userId);
        storage.delete(userId);
    }

    public User getById(Integer id) {
        log.info("Requested user with ID = " + id);
        return storage.getById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        checkUser(userId, friendId);
        storage.addFriend(userId, friendId);

        log.info("Friend successfully added");
    }

    public void removeFriend(Integer userId, Integer friendId) {
        checkUser(userId, friendId);
        storage.removeFriend(userId, friendId);
        log.info("Friend successfully removed");
    }

    public List<User> getAllFriends(Integer userId) {
        checkUser(userId, userId);
        List<User> result = storage.getFriends(userId);
        log.info("Friends of user with ID = " + userId + result);
        return result;
    }

    public List<User> getCommonFriends(Integer user1Id, Integer user2Id) {
        checkUser(user1Id, user2Id);
        List<User> result = storage.getCommonFriends(user1Id, user2Id);
        log.info("Common friends of users with ID " + " {} and {} {} ", user1Id, user2Id, result);
        return result;
    }

    public List<Film> getRecommendations(int userId) {
        List<Film> recommendations = new ArrayList<>();
        List<Integer> usersLikesSameFilms = new ArrayList<>();
        if (storage.getById(userId) == null) {
            throw new NotFoundException("User with ID = " + userId + " not found");
        }
        User user = storage.getById(userId);
        Set<Integer> userLikes = storage.getById(userId).getLikes();
        if (userLikes.isEmpty()) {
            return recommendations;
        }
        for (Integer filmId : userLikes) {
            Film film = filmStorage.getById(filmId);
            usersLikesSameFilms.addAll(filmStorage.getById(filmId).getLikes());
        }
        Map<Integer, Long> repetitions = usersLikesSameFilms.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        repetitions.remove(userId);
        if (repetitions.isEmpty()) {
            return recommendations;
        }
        Optional<Integer> idUserWithSameLikes = repetitions.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
        User userWithSameLikes = storage.getById(idUserWithSameLikes.get());
        List<Integer> recInteger = new ArrayList<>();
        recInteger.addAll(userWithSameLikes.getLikes());
        recInteger.removeAll(storage.getById(userId).getLikes());
        for (Integer filmId : recInteger) {
            Film filmToRec = filmStorage.getById(filmId);
            recommendations.add(filmToRec);
        }
        log.info("For user {} recommended {} films from user {}.", userId, recommendations.size(),
                idUserWithSameLikes.get());

        return recommendations;
    }

    private void checkUser(Integer userId, Integer friendId) {
        storage.getById(userId);
        storage.getById(friendId);
    }

    private void validate(User user, String message) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug(message);
            throw new ValidationException(message);
        }
    }

    private void preSave(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
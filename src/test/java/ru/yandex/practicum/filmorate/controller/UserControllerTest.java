package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    public void start() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    void createEmptyName_shouldAddNameAsLogin() {
        int id = 1;
        String login = "sashajaaa";
        User user = new User(id, "sashajaaa@yandex.ru", login, null, LocalDate.now().minusYears(35));
        userController.create(user);

        assertEquals(login, userController.getUserById(id).getName());
    }

    @Test
    void updateUserNameToEmpty_shouldSetNameToLogin() {
        User user1 = new User(1, "sashajaaa@yandex.ru", "sashajaaa", "", LocalDate.now().minusYears(35));
        userController.create(user1);
        int id = 1;
        String login = "sashajaaa";
        User user = new User(id, "sashajaaa@yandex.ru", login, null, LocalDate.now().minusYears(35));
        userController.update(user);
        System.out.println(userController.getAll());

        assertEquals(login, userController.getUserById(id).getName());
    }
}
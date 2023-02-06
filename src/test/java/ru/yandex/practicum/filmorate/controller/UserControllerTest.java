package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    public void start() {
        userController = new UserController();
    }

    @Test
    void createEmptyName_shouldAddNameAsLogin() {
        int id = 1;
        String login = "sashajaaa";
        User user = new User(id, "sashajaaa@yandex.ru", login, null, LocalDate.now().minusYears(35));
        userController.create(user);

        assertEquals(login, userController.entities.get(id).getName());
    }

    @Test
    void updateUserNameToEmpty_shouldSetNameToLogin() {
        User user1 = new User(1, "sashajaaa@yandex.ru", "sashajaaa", "", LocalDate.now().minusYears(35));
        userController.create(user1);
        int id = 1;
        String login = "sashajaaa";
        User user = new User(id, "sashajaaa@yandex.ru", login, null, LocalDate.now().minusYears(35));
        userController.update(user);
        System.out.println(userController.entities);

        assertEquals(login, userController.entities.get(id).getName());
    }
}
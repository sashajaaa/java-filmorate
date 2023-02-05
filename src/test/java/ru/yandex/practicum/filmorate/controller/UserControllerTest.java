package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        assertEquals(login, userController.getUsers().get(id).getName());
    }

    @Test
    void createFutureBirthUser_shouldShowErrorMessage() {
        User user = new User(10, "sashajaaa@yandex.ru", "sashajaaa", "Aleksandr", LocalDate.now().plusYears(35));
        ValidationException e = assertThrows(ValidationException.class, () -> userController.create(user));

        assertEquals("The object form is filled in incorrectly", e.getMessage());
    }

    @Test
    void updateUserNameToEmpty_shouldSetNameToLogin() {
        User user1 = new User(1, "sashajaaa@yandex.ru", "sashajaaa", "", LocalDate.now().minusYears(35));
        userController.create(user1);
        int id = 1;
        String login = "sashajaaa";
        User user = new User(id, "sashajaaa@yandex.ru", login, null, LocalDate.now().minusYears(35));
        userController.update(user);
        System.out.println(userController.getUsers());

        assertEquals(login, userController.getUsers().get(id).getName());
    }

    @Test
    void updateFutureBirthUser_shouldShowErrorMessage() {
        User user1 = new User(10, "sashajaaa@yandex.ru", "sashajaaa", "Aleksandr", LocalDate.now().minusYears(35));
        userController.create(user1);
        User user = new User(10, "sashajaaa@yandex.ru", "sashajaaa", "Aleksandr", LocalDate.now().plusYears(5));
        ValidationException e = assertThrows(ValidationException.class, () -> userController.update(user));

        assertEquals("Object update form was filled out incorrectly", e.getMessage());
    }
}
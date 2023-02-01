package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends Controller<User> {

    @GetMapping
    @Override
    public Collection<User> findAll() {
        log.info("Current number of users: " + entities.size());
        return entities.values();
    }

    @PostMapping
    @Override
    public User create(@Valid @RequestBody User user) {
        super.create(user);
        fixVoidName(user);
        log.info("User successfully added: " + user);
        return user;
    }

    @PutMapping
    @Override
    public User update(@Valid @RequestBody User user) {
        super.update(user);
        fixVoidName(user);
        return user;
    }

    @Override
    public boolean validation(User user) {
        return !(user.getBirthday().isAfter(LocalDate.now()));
    }

    public void fixVoidName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            entities.put(user.getId(), user);
        }
    }

    public Map<Integer, User> getUsers() {
        return entities;
    }
}
package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Repository
@Qualifier("dataBase")
public class UserDbStorage extends AbstractStorage<User> implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "SELECT * FROM users";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery);
        List<User> users = new ArrayList<>();
        while (srs.next()) {
            users.add(userMap(srs));
        }
        return users;
    }

    @Override
    public User create(User user) {
        super.create(user);
        String sqlQuery = "INSERT INTO users (user_name, login, email, birthday)"
                + "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday());
        return user;
    }

    @Override
    public User update(User user) {
        getById(user.getId());
        String sqlQuery = "UPDATE users "
                + "SET user_name = ?, "
                + "login = ?, "
                + "email = ?, "
                + "birthday = ? "
                + "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getLogin(),
                user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User delete(int userId) {
        User user = getById(userId);
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        if (jdbcTemplate.update(sqlQuery, userId) == 0) {
            throw new NotFoundException("User with ID = " + userId + " not found!");
        }
        return user;
    }

    @Override
    public User getById(Integer userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (srs.next()) {
            return userMap(srs);
        } else {
            throw new NotFoundException("User with ID=" + userId + " not found!");
        }
    }

    public void addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) "
                + "VALUES(?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, true);
    }

    public void removeFriend(int userId, int friendId) {
        String sqlQuery = "DELETE friends "
                + "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        List<User> listOfFriends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users "
                + "WHERE users.user_id IN (SELECT friend_id from friends "
                + "WHERE user_id = ?)";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        while (srs.next()) {
            listOfFriends.add(UserDbStorage.userMap(srs));
        }
        return listOfFriends;
    }

    public List<User> getCommonFriends(int friend1, int friend2) {
        List<User> commonFriends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users "
                + "WHERE users.user_id IN (SELECT friend_id from friends "
                + "WHERE user_id IN (?, ?) "
                + "AND friend_id NOT IN (?, ?))";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, friend1, friend2, friend1, friend2);
        while (srs.next()) {
            commonFriends.add(UserDbStorage.userMap(srs));
        }
        return commonFriends;
    }

    public boolean isFriend(int userId, int friendId) {
        String sqlQuery = "SELECT * FROM friends WHERE "
                + "user_id = ? AND friends_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        return srs.next();
    }

    public static User userMap(SqlRowSet srs) {
        int id = srs.getInt("user_id");
        String name = srs.getString("user_name");
        String login = srs.getString("login");
        String email = srs.getString("email");
        LocalDate birthday = Objects.requireNonNull(srs.getTimestamp("birthday"))
                .toLocalDateTime().toLocalDate();
        return User.builder()
                .id(id)
                .name(name)
                .login(login)
                .email(email)
                .birthday(birthday)
                .build();
    }
}
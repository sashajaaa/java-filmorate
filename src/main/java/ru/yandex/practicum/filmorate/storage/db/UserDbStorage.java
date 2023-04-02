package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class UserDbStorage implements UserStorage {
    private static final String GET_USER_ID = "SELECT user_id FROM users WHERE user_id=?";

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
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("users")
                .usingColumns("user_name", "login", "email", "birthday")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKeyHolder(Map.of(
                        "user_name", user.getName(),
                        "login", user.getLogin(),
                        "email", user.getEmail(),
                        "birthday", java.sql.Date.valueOf(user.getBirthday())))
                .getKeys();
        user.setId((Integer) keys.get("user_id"));
        return user;
    }

    @Override
    public User update(User user) {
        //getById(user.getId());
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
        jdbcTemplate.execute("DELETE FROM users WHERE user_id = " + userId);
        return user;
    }

    @Override
    public User getById(int userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        return  jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public boolean containsUser(int id) {
        return jdbcTemplate.queryForRowSet(GET_USER_ID, id).next();
    }

    public void addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) "
                + "VALUES(?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, true);
    }

    public void removeFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM friends "
                + "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users "
                + "WHERE users.user_id IN (SELECT friend_id FROM friends "
                + "WHERE user_id = ?)";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        while (srs.next()) {
            friends.add(UserDbStorage.userMap(srs));
        }
        return friends;
    }

    public List<User> getCommonFriends(int friend1, int friend2) {
        List<User> commonFriends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users "
                + "WHERE users.user_id IN (SELECT friend_id FROM friends "
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
                + "user_id = ? AND friend_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        return srs.next();
    }

    private static User userMap(SqlRowSet srs) {
        int id = srs.getInt("USER_ID");
        String name = srs.getString("USER_NAME");
        String login = srs.getString("LOGIN");
        String email = srs.getString("EMAIL");
        LocalDate birthday = Objects.requireNonNull(srs.getTimestamp("BIRTHDAY"))
                .toLocalDateTime().toLocalDate();
        return User.builder()
                .id(id)
                .name(name)
                .login(login)
                .email(email)
                .birthday(birthday)
                .build();
    }

    private User mapRowToUser(ResultSet rs, long rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
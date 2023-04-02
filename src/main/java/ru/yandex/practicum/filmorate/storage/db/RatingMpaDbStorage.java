package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RatingMpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingMpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RatingMpa getRatingMpaById(int ratingId) {
        String sqlQuery = "SELECT * FROM rating_mpa WHERE rating_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, ratingId);
            return new RatingMpa(ratingId, srs.getString("rating_name"));
    }

    public List<RatingMpa> getRatingsMpa() {
        List<RatingMpa> ratingsMpa = new ArrayList<>();
        String sqlQuery = "SELECT * FROM rating_mpa";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery);
        while (srs.next()) {
            ratingsMpa.add(new RatingMpa(srs.getInt("rating_id"), srs.getString("rating_name")));
        }
        return ratingsMpa;
    }

    public boolean containsMpa(int id) {
        return jdbcTemplate.queryForRowSet("SELECT * FROM rating_mpa WHERE rating_id = ?", id).next();
    }
}
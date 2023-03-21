package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addGenres(Film film, List<Genre> genres) {
        int filmID = film.getId();
        deleteAllGenresById(filmID);
        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) " + "VALUES(?, ?)";
        this.jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmID);
                ps.setInt(2, genres.get(i).getId());
            }

            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    @Override
    public void deleteAllGenresById(int filmId) {
        String sglQuery = "DELETE film_genres WHERE genre_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, genreId);
        if (srs.next()) {
            return new Genre(genreId, srs.getString("genre_name"));
        }
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        String sqlQuery = "SELECT * FROM genres ";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery);
        while (srs.next()) {
            genres.add(new Genre(srs.getInt("genre_id"), srs.getString("genre_name")));
        }
        return genres;
    }
}
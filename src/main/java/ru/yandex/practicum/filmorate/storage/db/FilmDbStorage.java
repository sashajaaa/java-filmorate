package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Qualifier("dataBase")
public class FilmDbStorage extends AbstractStorage<Film> implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAll() {
        String sqlQuery = "SELECT * FROM films "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + "LEFT JOIN film_genres ON film_genres.film_id = films.film_id "
                + "LEFT JOIN genres ON genres.genre_id = film_genres.genre_id";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        return addGenreForList(films);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (film_name, description, duration, release_date, rating_id)"
                + "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getDuration(),
                film.getReleaseDate(), film.getMpa().getId());
        addGenre(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        getById(film.getId());
        String sqlQuery = "UPDATE films "
                + "SET film_name = ?, "
                + "description = ?, "
                + "duration = ?, "
                + "release_date = ?, "
                + "rating_id = ? "
                + "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getDuration(),
                film.getReleaseDate(), film.getMpa().getId(), film.getId());
        addGenre(film.getId(), film.getGenres());
        int filmId = film.getId();
        film.setGenres(getGenres(filmId));
        return getById(filmId);
    }

    @Override
    public Film delete(int filmId) {
        Film film = getById(filmId);
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        if (jdbcTemplate.update(sqlQuery, filmId) == 0) {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
        return film;
    }

    @Override
    public Film getById(Integer filmId) {
        String sqlQuery = "SELECT * FROM films "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + "WHERE film_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (srs.next()) {
            return filmMap(srs);
        } else {
            throw new NotFoundException("Movie with ID = " + filmId + " not found");
        }
    }

    public void addGenre(int filmId, Set<Genre> genres) {
        deleteAllGenresById(filmId);
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) "
                + "VALUES (?, ?)";
        List<Genre> genresList = new ArrayList<>(genres);
        this.jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genresList.get(i).getId());
            }
            public int getBatchSize() {
                return genresList.size();
            }
        });
    }

    public Set<Genre> getGenres(int filmId) {
        Comparator<Genre> compId = Comparator.comparing(Genre::getId);
        Set<Genre> genres = new TreeSet<>(compId);
        String sqlQuery = "SELECT film_genres.genre_id, genres.genre_name FROM film_genres "
                + "JOIN genres ON genres.genre_id = film_genres.genre_id "
                + "WHERE film_id = ? ORDER BY genre_id ASC";
        genres.addAll(jdbcTemplate.query(sqlQuery, this::makeGenre, filmId));
        return genres;
    }

    public void deleteAllGenresById(int filmId) {
        String sglQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) "
                + "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE likes " + "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public List<Film> getSortedFilms() {
        String sqlQuery = "SELECT * FROM films "
                + "LEFT JOIN likes ON likes.film_id = films.film_id "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + "GROUP BY films.film_id "
                + "ORDER BY COUNT (likes.film_id) DESC";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public void likeCheck(Integer filmId, Integer userId) {
        getById(filmId);
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (srs.next()) {
            log.info("Like validation is complete");
        } else {
            throw new NotFoundException("User with ID = " + userId + " not found");
        }
    }

    private List<Film> addGenreForList(List<Film> films) {
        Map<Integer, Film> filmsMap = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
        String inSql = String.join(", ", Collections.nCopies(filmsMap.size(), "?"));
        final String sqlQuery = "SELECT * "
                + "FROM film_genres "
                + "LEFT OUTER JOIN genres ON film_genres.genre_id = genres.genre_id "
                + "WHERE film_genres.film_id IN (" + inSql + ") "
                + "ORDER BY film_genres.genre_id";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            filmsMap.get(rs.getInt("film_id")).addGenre(new Genre(rs.getInt("genre_id"),
                    rs.getString("genre_name")));
        }, filmsMap.keySet().toArray());
        return films;
    }

    private Genre makeGenre(ResultSet rs, int id) throws SQLException {
        int genreId = rs.getInt("genre_id");
        String genreName = rs.getString("genre_name");
        return new Genre(genreId, genreName);
    }

    private Film makeFilm(ResultSet rs, int id) throws SQLException {
        int filmId = rs.getInt("film_id");
        String name = rs.getString("film_name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        LocalDate releaseDate = rs.getTimestamp("release_date").toLocalDateTime().toLocalDate();
        int mpaId = rs.getInt("rating_id");
        String mpaName = rs.getString("rating_name");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Genre> genres = new HashSet<>();
        return Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .releaseDate(releaseDate)
                .build();
    }

    public Film filmMap(SqlRowSet srs) {
        int id = srs.getInt("film_id");
        String name = srs.getString("film_name");
        String description = srs.getString("description");
        int duration = srs.getInt("duration");
        LocalDate releaseDate = Objects.requireNonNull(srs.getTimestamp("release_date"))
                .toLocalDateTime().toLocalDate();
        int mpaId = srs.getInt("rating_id");
        String mpaName = srs.getString("rating_name");
        RatingMpa mpa = new RatingMpa(mpaId, mpaName);
        Set<Genre> genres = getGenres(id);
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .releaseDate(releaseDate)
                .build();
    }
}
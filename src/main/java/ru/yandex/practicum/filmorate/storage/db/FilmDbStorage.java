package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${director.get-filmsId-sorted-by-likes}")
    private String requestFilmIdByLikes;
    @Value("${director.get-filmsId-sorted-by-year}")
    private String requestFilmIdByYear;

    @Override
    public Collection<Film> getAll() {
        SqlRowSet filmsIdRow = jdbcTemplate.queryForRowSet("SELECT film_id FROM films");
        List<Film> films = new ArrayList<>();
        while (filmsIdRow.next()) {
            films.add(getById(filmsIdRow.getInt("film_id")));
        }
        Collections.sort(films, (f1, f2) -> f1.getId() - f2.getId());
        return films;
    }

    @Override
    public Film create(Film film) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("films")
                .usingColumns("film_name", "description", "duration", "release_date", "rating_id")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKeyHolder(Map.of("film_name", film.getName(),
                        "description", film.getDescription(),
                        "duration", film.getDuration(),
                        "release_date", java.sql.Date.valueOf(film.getReleaseDate()),
                        "rating_id", film.getMpa().getId()))
                .getKeys();
        film.setId((Integer) keys.get("film_id"));
        addGenre((Integer) keys.get("film_id"), film.getGenres());
        addDirectors((Integer) keys.get("film_id"), film.getDirectors());
        Film newFilm = getById((Integer) keys.get("film_id"));
        System.out.println(newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film film) {
        Film buffFilm = getById(film.getId());
        int filmId;
        try {
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
            addDirectors(film.getId(), film.getDirectors());
            filmId = film.getId();
            film.setGenres(getGenres(filmId));
            film.setDirectors(getDirectors(filmId));
        } catch (DataIntegrityViolationException | BadSqlGrammarException ex) {
            update(buffFilm);
            throw new RuntimeException("SQL exception");
        }
        Film updatedFilm = getById(filmId);
        return updatedFilm;
    }

    @Override
    public Film delete(Integer filmId) {
        Film film = getById(filmId);
        jdbcTemplate.execute("DELETE FROM films WHERE film_id = " + filmId);
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

    @Override
    public List<Film> getDirectorsFilms(int directorId, String sortBy) {
        SqlRowSet filmIdRow;
        if ("year".equals(sortBy)) {
            filmIdRow = jdbcTemplate.queryForRowSet(requestFilmIdByYear, directorId);
        } else {
            filmIdRow = jdbcTemplate.queryForRowSet(requestFilmIdByLikes, directorId);
        }
        List<Film> films = new LinkedList<>();
        while (filmIdRow.next()) {
            films.add(getById(filmIdRow.getInt("film_id")));
        }
        if (films.size() == 0) {
            log.info("Director's {} films are not found", directorId);
            throw new NotFoundException("Director's " + directorId + " films are not found");
        }
        log.info("Director's {} list of films{} sorted by {} is returned", directorId, films,sortBy);
        return films;
    }

    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) "
                + "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE likes "
                + "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        String sqlQuery = "SELECT * FROM films "
                + "LEFT JOIN likes ON likes.film_id = films.film_id "
                + "JOIN rating_mpa ON films.rating_id = rating_mpa.rating_id "
                + "GROUP BY films.film_id, "
                + "likes.user_id "
                + "ORDER BY COUNT (likes.film_id) DESC "
                + "LIMIT "
                + count;
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    public void addGenre(int filmId, Set<Genre> genres) {
        deleteAllGenresById(filmId);
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) "
                + "VALUES (?, ?)";
        List<Genre> genresTable = new ArrayList<>(genres);
        this.jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genresTable.get(i).getId());
            }
            public int getBatchSize() {
                return genresTable.size();
            }
        });
    }

    private Set<Genre> getGenres(int filmId) {
        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        String sqlQuery = "SELECT film_genres.genre_id, genres.genre_name FROM film_genres "
                + "JOIN genres ON genres.genre_id = film_genres.genre_id "
                + "WHERE film_id = ? ORDER BY genre_id ASC";
        genres.addAll(jdbcTemplate.query(sqlQuery, this::makeGenre, filmId));
        return genres;
    }

    private void deleteAllGenresById(int filmId) {
        String sglQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    private void addDirectors(int filmId, Set<Director> directors) {

        deleteAllDirectorsByFilmId(filmId);
        if (directors == null || directors.isEmpty()) {
            return;
        }
        List<Director> directorList = new ArrayList<>(directors);
        jdbcTemplate.batchUpdate("INSERT INTO films_directors (film_id, director_id) VALUES(?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, directorList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return directorList.size();
                    }
                });
    }

    private Set<Director> getDirectors(int filmId) {
        Set<Director> directors = new TreeSet<>(Comparator.comparingInt(Director::getId));
        String sqlQuery = "SELECT films_directors.director_id, directors.director_name FROM films_directors "
                + "JOIN directors ON directors.director_id = films_directors.director_id "
                + "WHERE film_id = ? ORDER BY director_id ASC";
        directors.addAll(jdbcTemplate.query(sqlQuery, this::makeDirector, filmId));
        return directors;
    }

    private void deleteAllDirectorsByFilmId(int filmId) {
        String sglQuery = "DELETE FROM films_directors WHERE film_id = ?";
        jdbcTemplate.update(sglQuery, filmId);
    }

    private Set<Integer> getLikes(int filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> foundFilmLikes = jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
        Set<Integer> likes = new HashSet<>(foundFilmLikes);
        return likes;
    }

    private Genre makeGenre(ResultSet rs, int id) throws SQLException {
        int genreId = rs.getInt("genre_id");
        String genreName = rs.getString("genre_name");
        return new Genre(genreId, genreName);
    }

    private Director makeDirector(ResultSet rs, int id) throws SQLException {
        Integer directorId = rs.getInt("director_id");
        String directorName = rs.getString("director_name");
        return new Director(directorId, directorName);
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
        Set<Genre> genres = getGenres(filmId);
        Set<Director> directors = new HashSet<>();
        return Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .releaseDate(releaseDate)
                .directors(directors)
                .build();
    }

    private Film filmMap(SqlRowSet srs) {
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
        Set<Director> directors = getDirectors(id);
        Set<Integer> likes = getLikes(id);
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .releaseDate(releaseDate)
                .directors(directors)
                .likes(likes)
                .build();
    }
}
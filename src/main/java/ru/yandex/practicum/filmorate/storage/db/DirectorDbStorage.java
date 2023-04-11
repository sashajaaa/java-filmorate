package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BuildException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    @Value("${director.get-id-by-director}")
    private String requestGetIdByDirector;
    @Value("${director.insert-director}")
    private String requestInsertDirector;
    @Value("${director.get-director-by-id}")
    private String requestGetDirectorById;
    @Value("${director.get-all-directors}")
    private String requestAllDirectors;
    @Value("${director.delete-all-directors}")
    private String requestDeleteAllDirectors;
    @Value("${director.delete-director-by-id}")
    private String requestDeleteById;
    @Value("${director.update-director}")
    private String requestUpdateDirector;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director addDirector(Director director) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(requestInsertDirector, new String[]{"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public List<Director> getAllDirectors() {
        Map<Integer, Director> directors = new LinkedHashMap<>();
        jdbcTemplate.query(requestAllDirectors,
                rs -> {
                    int id = rs.getInt("director_id");
                    directors.computeIfAbsent(id, m -> collectDirector(rs));
                });
        return new ArrayList<>(directors.values());
    }

    private Director collectDirector(ResultSet rs) {
        Director director;
        try {
            director = Director.builder()
                    .name(rs.getString("director_name"))
                    .id(rs.getInt("director_id"))
                    .build();
        } catch (Exception e) {
            throw new BuildException("An error occurred while assembling the director");
        }
        return director;
    }

    @Override
    public Director getDirectorById(Integer id) {

        return jdbcTemplate.query(requestGetDirectorById,
                ps -> ps.setInt(1, id),
                rs -> {
                    if (rs.next()) {
                        return collectDirector(rs);
                    } else {
                        return null;
                    }
                }
        );

    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update(requestUpdateDirector, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public Director deleteDirectorById(Integer id) {

        Director removedDirector = getDirectorById(id);
        jdbcTemplate.execute(requestDeleteById + id);
        return removedDirector;
    }

    @Override
    public void deleteAllDirectors() {
        jdbcTemplate.execute(requestDeleteAllDirectors);
    }

    @Override
    public boolean containsDirector(int id) {
        return jdbcTemplate.queryForRowSet(requestGetDirectorById, id).next();
    }

    public boolean isPresentInDb(Director director) {
        return jdbcTemplate.queryForRowSet(requestGetIdByDirector, director.getName()).next();
    }

}
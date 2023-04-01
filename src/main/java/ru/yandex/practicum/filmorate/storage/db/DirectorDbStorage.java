package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.util.ArrayList;
import java.util.List;

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
    @Value("${director.get-all-IDs}")
    private String requestAllIDs;
    @Value("${director.delete-director-by-id}")
    private String requestDeleteById;
    @Value("${director.reset-primary-key}")
    private String requestResetPK;
    @Value("${director.update-director}")
    private String requestUpdateDirector;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director addDirector(Director director) {
        jdbcTemplate.update(requestInsertDirector, director.getName());
        SqlRowSet idRow = getIdRowsFromDB(director);
        if (idRow.next()) {
            director.setId(idRow.getInt("director_id"));
        }
        return getDirectorById(director.getId());
    }

    @Override
    public List<Director> getAllDirectors() {
        List<Director> directors = new ArrayList<>();
        SqlRowSet idsRow = jdbcTemplate.queryForRowSet(requestAllIDs);
        while (idsRow.next()) {
            Integer id = idsRow.getInt("director_id");
            directors.add(getDirectorById(id));
        }
        return directors;
    }

    @Override
    public Director getDirectorById(Integer id) {

        SqlRowSet directorRow = getDirectorRowByID(id);
        if (!directorRow.next()) {
            throw new NotFoundException("Не удалось передать режиссера: режиссер не найден");
        }
        Director director = buildDirectorFromRow(directorRow);
        log.info("Передан режиссер {}", director);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        int id = director.getId();
        jdbcTemplate.update(requestUpdateDirector, director.getName(), id);
        return getDirectorById(id);
    }

    @Override
    public Director deleteDirectorById(Integer id) {
        SqlRowSet directorRow = getDirectorRowByID(id);
        Director removedDirector = Director.builder()
                .id(directorRow.getInt("director_id"))
                .name(directorRow.getString("director_name"))
                .build();
        jdbcTemplate.execute(requestDeleteById + id);
        if (getAllDirectors().isEmpty()) {
            jdbcTemplate.execute(requestResetPK);
        }
        return removedDirector;
    }

    @Override
    public void deleteAllDirectors() {
        SqlRowSet idsRow = jdbcTemplate.queryForRowSet(requestAllIDs);
        while (idsRow.next()) {
            jdbcTemplate.execute(requestDeleteById + idsRow.getInt("director_id"));
        }
        jdbcTemplate.execute(requestResetPK);
    }

    @Override
    public boolean containsDirector(int id) {
        return jdbcTemplate.queryForRowSet(requestGetDirectorById, id).next();
    }

    private Director buildDirectorFromRow(SqlRowSet row) {
        return Director.builder()
                .name(row.getString("director_name"))
                .id(row.getInt("director_id"))
                .build();
    }

    private SqlRowSet getDirectorRowByID(Integer id) {
        return jdbcTemplate.queryForRowSet(requestGetDirectorById, id);
    }

    private SqlRowSet getIdRowsFromDB(Director director) {
        return jdbcTemplate.queryForRowSet(requestGetIdByDirector, director.getName());
    }
}

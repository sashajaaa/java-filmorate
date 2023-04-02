package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
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
        if (isPresentInDB(director)) {
            throw new ObjectAlreadyExistsException("Unable to add director: director already exists");
        }
        jdbcTemplate.update(requestInsertDirector, director.getName());
        SqlRowSet idRow = getIdRowsFromDB(director);
        if (idRow.next()) {
            director.setId(idRow.getInt("director_id"));
        }
        log.info("Director {} is added", director);
        return director;
    }

    @Override
    public List<Director> getAllDirectors() {
        List<Director> directors = new ArrayList<>();
        SqlRowSet idsRow = jdbcTemplate.queryForRowSet(requestAllIDs);
        while (idsRow.next()) {
            Integer id = idsRow.getInt("director_id");
            directors.add(getDirectorById(id));
        }
        log.info("List of directors is returned");
        return directors;
    }

    @Override
    public Director getDirectorById(Integer id) {

        SqlRowSet directorRow = getDirectorRowByID(id);
        if (!directorRow.next()) {
            throw new NotFoundException("Unable to return director: director is not found");
        }
        Director director = buildDirectorFromRow(directorRow);
        log.info("Director is returned {}", director);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {

        if (!isPresentInDB(director.getId())) {
            throw new NotFoundException("Unable to update director: director is not found");
        }
        if (isPresentInDB(director)) {
            throw new NotFoundException("Unable to update director: director is not found");
        }
        Director bufferDirector = getDirectorById(director.getId());
        try {
            jdbcTemplate.update(requestUpdateDirector, director.getName(), director.getId());
        } catch (DataIntegrityViolationException | BadSqlGrammarException ex) {
            updateDirector(bufferDirector);
            throw new RuntimeException("SQL exception");
        }
        Director updatedDirector = getDirectorById(director.getId());
        log.info("Director is updated {}", updatedDirector);
        return updatedDirector;
    }

    @Override
    public Director deleteDirectorById(Integer id) {

        SqlRowSet directorRow = getDirectorRowByID(id);
        if (!directorRow.next()) {
            throw new NotFoundException("Unable to delete director: director is not found");
        }
        Director removedDirector = Director.builder()
                .id(directorRow.getInt("director_id"))
                .name(directorRow.getString("director_name"))
                .build();
        jdbcTemplate.execute(requestDeleteById + id);
        if (getAllDirectors().isEmpty()) {
            jdbcTemplate.execute(requestResetPK);
        }
        log.info("Director is deleted {}", removedDirector);
        return removedDirector;
    }

    @Override
    public void deleteAllDirectors() {
        SqlRowSet idsRow = jdbcTemplate.queryForRowSet(requestAllIDs);
        while (idsRow.next()) {
            jdbcTemplate.execute(requestDeleteById + idsRow.getInt("director_id"));
        }
        jdbcTemplate.execute(requestResetPK);
        log.info("Directors table is dropped");
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

    private boolean isPresentInDB(Director director) {
        return getIdRowsFromDB(director).next();
    }

    private boolean isPresentInDB(Integer id) {
        return getDirectorRowByID(id).next();
    }

    private SqlRowSet getDirectorRowByID(Integer id) {
        return jdbcTemplate.queryForRowSet(requestGetDirectorById, id);
    }

    private SqlRowSet getIdRowsFromDB(Director director) {
        return jdbcTemplate.queryForRowSet(requestGetIdByDirector, director.getName());
    }
}
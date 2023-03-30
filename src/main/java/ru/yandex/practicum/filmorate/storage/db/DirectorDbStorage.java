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
import ru.yandex.practicum.filmorate.storage.interfaces.DirecorStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DirectorDbStorage implements DirecorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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


    @Override
    public Director addDirector(Director director) {

        if (isPresentInDB(director)) {
            throw new ObjectAlreadyExistsException("Не удалось добавить режиссера: режиссер уже существует");
        }
        jdbcTemplate.update(requestInsertDirector, director.getName());
        SqlRowSet idRow = getIdRowsFromDB(director);
        if (idRow.next()) {
            director.setId(idRow.getInt("director_id"));
        }
        log.info("Добавлен режиссер {}", director);
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
        log.info("Передан список всех пользователей");
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

        if (!isPresentInDB(director.getId())) {
            throw new NotFoundException("Не удалось обновить режиссера: режиссер не найден");
        }
        if (isPresentInDB(director)) {
            throw new NotFoundException("Не удалось обновить режиссера: режиссер уже существует");
        }
        Director bufferDirector = getDirectorById(director.getId());
        try {
            jdbcTemplate.update(requestUpdateDirector, director.getName(), director.getId());
        } catch (DataIntegrityViolationException | BadSqlGrammarException ex) {
            updateDirector(bufferDirector);
            throw new RuntimeException("SQL exception");
        }
        Director updatedDirector = getDirectorById(director.getId());
        log.info("Обновлен режиссер {}", updatedDirector);
        return updatedDirector;

    }

    @Override
    public Director deleteDirectorById(Integer id) {

        SqlRowSet directorRow = getDirectorRowByID(id);
        if (!directorRow.next()) {
            throw new NotFoundException("Не удалось удалить режиссера: режиссер не найден");
        }
        Director removedDirector = Director.builder()
                .id(directorRow.getInt("director_id"))
                .name(directorRow.getString("director_name"))
                .build();
        jdbcTemplate.execute(requestDeleteById + id);
        if (getAllDirectors().size() == 0) {
            jdbcTemplate.execute(requestResetPK);
        }
        log.info("Удален режиссер {}", removedDirector);
        return removedDirector;

    }

    @Override
    public void deleteAllDirectors() {

        SqlRowSet idsRow = jdbcTemplate.queryForRowSet(requestAllIDs);
        while (idsRow.next()) {
            jdbcTemplate.execute(requestDeleteById + idsRow.getInt("director_id"));
        }
        jdbcTemplate.execute(requestResetPK);
        log.info("Таблица режиссеров очищена");

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
        SqlRowSet rows = jdbcTemplate.queryForRowSet(requestGetIdByDirector, director.getName());
        return rows;
    }
}

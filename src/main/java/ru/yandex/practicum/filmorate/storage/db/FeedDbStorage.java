package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BuildException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${feed.addFeed}")
    private String requestAddFeed;
    @Value("${feed.find-by-timeStamp}")
    private String requestFindByTimeStamp;
    @Value("${feed.get-all-feed-of-user}")
    private String requestGetAllFeedOfUser;

    @Override
    public Optional<Feed> addFeed(Integer userId, EventType eventType, OperationType operation, Integer entityId) {

        Long timeStamp = Instant.now().toEpochMilli();
        jdbcTemplate.update(requestAddFeed,
                timeStamp,
                entityId,
                userId,
                eventType.ordinal() + 1,
                operation.ordinal() + 1
        );
        return getFeedByTimeStamp(timeStamp);
    }

    @Override
    public List<Feed> getAllFeedByUserId(Integer userId) {
        Map<Long, Feed> feedMap = new LinkedHashMap<>();

        jdbcTemplate.query(requestGetAllFeedOfUser,
                ps -> ps.setLong(1, userId),
                rs -> {
                    Long id = rs.getLong("event_id");
                    feedMap.computeIfAbsent(id, m -> collectFeed(rs));
                });
        return new ArrayList<>(feedMap.values());
    }

    private Feed collectFeed(ResultSet rs) {
        Feed feed;
        try {
            feed = Feed.builder()
                    .timestamp(rs.getLong("event_timestamp"))
                    .entityId(rs.getInt("entity_id"))
                    .eventId(rs.getInt("event_id"))
                    .userId(rs.getInt("user_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type_name")))
                    .operation(OperationType.valueOf(rs.getString("operation_type_name")))
                    .build();
        } catch (Exception e) {
            throw new BuildException("An error occurred while assembling the feed");
        }
        return feed;
    }


    @Override
    public Optional<Feed> getFeedByTimeStamp(Long timeStamp) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(requestFindByTimeStamp, timeStamp);

        if (rowSet.next()) {
            Feed feed = Feed.builder()
                    .timestamp(rowSet.getLong("event_timestamp"))
                    .entityId(rowSet.getInt("entity_id"))
                    .eventId(rowSet.getInt("event_id"))
                    .userId(rowSet.getInt("user_id"))
                    .eventType(EventType.valueOf(rowSet.getString("event_type_name")))
                    .operation(OperationType.valueOf(rowSet.getString("operation_type_name")))
                    .build();
            return Optional.of(feed);
        }
        return Optional.empty();
    }
}

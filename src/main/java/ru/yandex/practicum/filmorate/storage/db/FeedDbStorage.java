package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BuildException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;

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
    public Optional<Feed> addFeed(Long userId, Feed.EventType eventType, Feed.OperationType operation, Long entityId) {

        Instant timeStamp = Instant.now();
        System.out.println(timeStamp);
        jdbcTemplate.update(requestAddFeed
                , timeStamp.toEpochMilli()
                , entityId
                , userId
                , eventType.ordinal() + 1
                , operation.ordinal() + 1
        );
        return getFeedByTimeStamp(timeStamp);
    }

    @Override
    public List<Feed> getAllFeedByUserId(Long userId) {
        Map<Long, Feed> feedMap = new LinkedHashMap<>();

        jdbcTemplate.query(requestGetAllFeedOfUser,
                ps -> {
                    ps.setLong(1, userId);
                },
                rs -> {
                    Long id = rs.getLong("event_id");
                    feedMap.computeIfAbsent(id, m -> collectFeed(rs));
                });
        System.out.println(feedMap);
        return new ArrayList<Feed>(feedMap.values());
    }

    private Feed collectFeed(ResultSet rs) {
        Feed feed;
        try {
            feed = Feed.builder()
                    .timestamp(rs.getLong("event_timestamp"))
                    .entityId(rs.getLong("entity_id"))
                    .eventId(rs.getLong("event_id"))
                    .userId(rs.getLong("user_id"))
                    .eventType(Feed.EventType.valueOf(rs.getString("event_type_name")))
                    .operation(Feed.OperationType.valueOf(rs.getString("operation_type_name")))
                    .build();
        } catch (Exception e) {
            throw new BuildException("An error occurred while assembling the feed");
        }
        return feed;
    }


    @Override
    public Optional<Feed> getFeedByTimeStamp(Instant timeStamp) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(requestFindByTimeStamp, timeStamp.toEpochMilli());

        if (rowSet.next()) {
            Feed feed = Feed.builder()
                    .timestamp(rowSet.getLong("event_timestamp"))
                    .entityId(rowSet.getLong("entity_id"))
                    .eventId(rowSet.getLong("event_id"))
                    .userId(rowSet.getLong("user_id"))
                    .eventType(Feed.EventType.valueOf(rowSet.getString("event_type_name")))
                    .operation(Feed.OperationType.valueOf(rowSet.getString("operation_type_name")))
                    .build();
            return Optional.of(feed);
        }
        return Optional.empty();
    }
}

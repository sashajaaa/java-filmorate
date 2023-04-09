package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Feed;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FeedStorage {

    public Optional<Feed> addFeed(Long userId, Feed.EventType eventType, Feed.OperationType operation, Long entityId);

    public List<Feed> getAllFeedByUserId(Long userId);

    public Optional<Feed> getFeedByTimeStamp(Instant timeStamp);
}

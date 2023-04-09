package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FeedStorage {

    Optional<Feed> addFeed(Long userId, EventType eventType, OperationType operation, Long entityId);

    List<Feed> getAllFeedByUserId(Long userId);

    Optional<Feed> getFeedByTimeStamp(Instant timeStamp);
}
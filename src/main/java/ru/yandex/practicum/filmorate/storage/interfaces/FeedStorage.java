package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FeedStorage {

    Optional<Feed> addFeed(Integer userId, EventType eventType, OperationType operation, Integer entityId);

    List<Feed> getAllFeedByUserId(Integer userId);

    Optional<Feed> getFeedByTimeStamp(Long timeStamp);
}
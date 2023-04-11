package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    @Autowired
    public FeedService(FeedStorage feedStorage, UserStorage userStorage) {
        this.feedStorage = feedStorage;
        this.userStorage = userStorage;
    }

    public Feed addFeed(Integer userId, EventType eventType, OperationType operation, Integer entityId) {
        Optional<Feed> feed = feedStorage.addFeed(userId, eventType, operation, entityId);
        log.info("feed added {}", feed);
        return Optional.of(feed).get().orElseThrow(
                ()->new RuntimeException("An error occurred while adding a feed event"));
    }

    public List<Feed> getAllFeedByUserId(Integer userId) {
        containsUser(userId);
        List<Feed> feed = feedStorage.getAllFeedByUserId(userId);
        log.info("All feed of user {} is returned", userId);
        return feed;
    }

    private void containsUser(Integer userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException(String.format("User by id '%d' not found", userId));
        }
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
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

    public Feed addFeed(Long userId, Feed.EventType eventType, Feed.OperationType operation, Long entityId) {
        Optional<Feed> feed = feedStorage.addFeed(userId, eventType, operation, entityId);
        if (feed.isEmpty()) {
            throw new RuntimeException("An error occurred while adding a feed event");
        }
        log.info("feed added {}", feed.get());
        return feed.get();
    }

    public List<Feed> getAllFeedByUserId(Long userId) {
        throwUserNotFoundException(userId.intValue());
        List<Feed> feed = feedStorage.getAllFeedByUserId(userId);
        log.info("All feed of user {} is returned", userId);
        return feed;
    }

    private void throwUserNotFoundException(Integer userId) {
        try {
            userStorage.getById(userId);
        } catch (Exception e) {
            throw new NotFoundException(String.format("User by id '%d' not found", userId));
        }
    }

}

package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@ToString
public class Feed {
    @NotNull
    private Long timestamp;

    @NotNull
    private Integer userId;

    @NotNull
    private EventType eventType;

    @NotNull
    private OperationType operation;

    private Integer eventId;

    @NotNull
    private Integer entityId;
}
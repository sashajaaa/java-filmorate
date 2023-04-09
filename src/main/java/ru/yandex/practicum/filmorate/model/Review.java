package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@Data
@With
@Value
@Builder
public class Review {
    Integer reviewId;

    @NotNull
    String content;

    @NotNull
    Boolean isPositive;

    @NotNull
    Integer userId;

    @NotNull
    Integer filmId;

    Integer useful;
}
package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.With;

@Data
@With
@Value
@Builder
public class Review {
	Integer reviewId;
	String content;
	Boolean isPositive;
	Integer userId;
	Integer filmId;
	Integer useful;
}

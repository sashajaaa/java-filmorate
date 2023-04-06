package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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
	@Positive
	Integer userId;

	@NotNull
	@Positive
	Integer filmId;

	Integer useful;
}

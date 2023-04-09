package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Film {
    private int id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @PositiveOrZero
    private int duration;

    private Set<Genre> genres = new HashSet<>();

    private RatingMpa mpa;

    private Set<Director> directors = new HashSet<>();

    @JsonIgnore
    private Set<Integer> likes = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
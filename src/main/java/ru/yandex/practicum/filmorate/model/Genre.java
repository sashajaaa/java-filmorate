package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Builder(toBuilder = true)
@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Genre {

    @Positive
    protected int id;

    @NotBlank
    protected String name;
}
package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class Director {

    private int id;
    @NotBlank(message = "Director`s name can not be blank")
    private String name;

}

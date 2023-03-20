package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
public class Genre extends ListModel {
    public Genre() {
    }

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
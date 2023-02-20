package ru.yandex.practicum.filmorate.utility;

public class IdGenerator {
    private Integer id;

    public IdGenerator() {
        this.id = 0;
    }

    public Integer getId() {
        this.id++;
        return this.id;
    }
}
package ru.yandex.practicum.filmorate.exception;

public class ObjectAlreadyExistsException extends RuntimeException {
    public ObjectAlreadyExistsException() {
    }

    public ObjectAlreadyExistsException(String message) {
        super(message);
    }

    public ObjectAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public ObjectAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
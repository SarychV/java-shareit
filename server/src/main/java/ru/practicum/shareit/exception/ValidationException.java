package ru.practicum.shareit.exception;

public class ValidationException extends IllegalArgumentException {
    public ValidationException() {
    }

    public ValidationException(String msg) {
        super(msg);
    }
}

package ru.practicum.shareit.exception;

public class ConflictException extends RuntimeException {
    public ConflictException() {
    }

    public ConflictException(String msg) {
        super(msg);
    }
}

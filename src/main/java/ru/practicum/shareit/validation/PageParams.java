package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.ValidationException;

public class PageParams {
    public static void validate(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Неверно задан диапазон страниц.");
        }
    }
}

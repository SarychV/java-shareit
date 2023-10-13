package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.exception.ValidationException;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class User {
    private Integer id;     // Уникальный идентификатор пользователя
    private String name;    // Имя или логи пользователя
    private String email;   // Уникальный адрес электронной почты пользователя

    public void hasValidEmailOrThrow() {
        if (email != null) {
            if (!email.contains("@")) {
                throw new ValidationException("Неверный формат адреса электронной почты.");
            }
        }
    }
}

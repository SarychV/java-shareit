package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;     // Уникальный идентификатор пользователя
    private String name;    // Имя или логи пользователя
    private String email;   // Уникальный адрес электронной почты пользователя
}

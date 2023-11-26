package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {
    private Integer id;     // Уникальный идентификатор пользователя
    private String name;    // Имя или логи пользователя
    @Email
    private String email;   // Уникальный адрес электронной почты пользователя
}

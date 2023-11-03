package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.exception.ValidationException;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;     // Уникальный идентификатор пользователя

    @Column
    private String name;    // Имя или логин пользователя

    @Column
    private String email;   // Уникальный адрес электронной почты пользователя

    public void hasValidEmailOrThrow() {
        if (email != null) {
            if (!email.contains("@")) {
                throw new ValidationException("Неверный формат адреса электронной почты.");
            }
        }
    }
}

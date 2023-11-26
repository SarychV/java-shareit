package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;

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
}

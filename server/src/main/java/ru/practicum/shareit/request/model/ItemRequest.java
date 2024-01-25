package ru.practicum.shareit.request.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // Уникальный идентификатор запроса

    @Column
    private String description;         // Текст запроса с описанием требуемой вещи

    @Column(name = "requester_id")
    private Integer requesterId;        // Пользователь, создавший запрос

    @Column(name = "create_date")
    private LocalDateTime created;      // Дата и время создания запроса
}

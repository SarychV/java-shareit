package ru.practicum.shareit.request.model;

/**
 * TODO Sprint add-item-requests.
 */

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // Уникальный идентификатор запроса

    private String description;     // Текст запроса с описанием требуемой вещи

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;         // Пользователь, создавший запрос

    private LocalDateTime created;  // Дата и время создания запроса
}

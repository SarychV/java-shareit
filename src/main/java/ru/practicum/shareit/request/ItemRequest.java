package ru.practicum.shareit.request;

/**
 * TODO Sprint add-item-requests.
 */

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;                // Уникальный идентификатор запроса
    private String description;     // Текст запроса с описанием требуемой вещи
    private User requester;         // Пользователь, создавший запрос
    private LocalDateTime created;  // Дата и время создания запроса
}

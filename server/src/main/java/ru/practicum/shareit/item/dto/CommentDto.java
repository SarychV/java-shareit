package ru.practicum.shareit.item.dto;

import lombok.Data;
import java.time.LocalDateTime;

// Класс для организации сведений о комментариях в ответах на запросы с использованием ItemDtoExtended
@Data
public class CommentDto {
    private Long id;                    // Идентификатор комментария
    private String text;                // Текст комментария
    private String authorName;          // Имя автора комментария
    private LocalDateTime created;      // Дата и время создания комментария
}

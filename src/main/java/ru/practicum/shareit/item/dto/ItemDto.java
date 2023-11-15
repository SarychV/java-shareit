package ru.practicum.shareit.item.dto;

/**
 * TODO Sprint add-controllers.
 */

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ItemDto {
    private final Long id;                // Уникальный идентификатор вещи
    private final String name;            // Название вещи
    private final String description;     // Развернутое описание
    private final Boolean available;      // Доступна вещь для аренды или нет
}
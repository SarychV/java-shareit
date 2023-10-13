package ru.practicum.shareit.item.dto;

/**
 * TODO Sprint add-controllers.
 */

import lombok.Data;

@Data
public class ItemDto {
    private final String name;            // Название вещи
    private final String description;     // Развернутое описание
    private final Boolean available;      // Доступна вещь для аренды или нет
    private       Integer owner;          // Владелец вещи
}
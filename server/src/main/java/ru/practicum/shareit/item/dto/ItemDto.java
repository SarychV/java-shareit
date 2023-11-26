package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;                // Уникальный идентификатор вещи
    private String name;            // Название вещи
    private String description;     // Развернутое описание
    private Boolean available;      // Доступна вещь для аренды или нет
}
package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class ItemDto {
    @Positive
    private Long id;                // Уникальный идентификатор вещи
    private String name;            // Название вещи
    private String description;     // Развернутое описание
    private Boolean available;      // Доступна вещь для аренды или нет
}
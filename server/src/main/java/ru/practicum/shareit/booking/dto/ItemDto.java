package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class ItemDto {
    private final Long id;          // Идентификатор вещи
    private final String name;      // Название вещи
}
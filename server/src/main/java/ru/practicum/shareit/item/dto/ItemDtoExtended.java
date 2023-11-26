package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemDtoExtended {
    private final Long id;                // Уникальный идентификатор вещи
    private final String name;            // Название вещи
    private final String description;     // Развернутое описание
    private final Boolean available;      // Доступна вещь для аренды или нет
    private BookingDto lastBooking;       // Сведения о последнем бронировании
    private BookingDto nextBooking;       // Сведения о следующем бронировании
    private List<CommentDto> comments;    // Комментарии к вещи
}

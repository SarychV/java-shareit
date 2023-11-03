package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDtoOut {
    private final Long id;                  // Уникальный идентификатор бронирования
    private final LocalDateTime start;      // Дата и время начала бронирования
    private final LocalDateTime end;        // Дата и время окончания бронирования
    private final BookingStatus status;     // Статус бронирования
    private final ItemDto item;             // Бронируемая вещь
    private final BookerDto booker;         // Пользователь, бронирующий вещь
}
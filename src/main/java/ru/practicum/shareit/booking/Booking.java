package ru.practicum.shareit.booking;

import lombok.Data;
/**
 * TODO Sprint add-bookings.
 */

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;                // Уникальный идентификатор бронирования
    private LocalDateTime start;    // Дата и время начала бронирования
    private LocalDateTime end;      // Дата и время окончания бронирования
    private Item item;              // Бронируемая вещь
    private User booker;            // Пользователь, бронирующий вещь
    private BookingStatus status;   // Статус бронирования
}
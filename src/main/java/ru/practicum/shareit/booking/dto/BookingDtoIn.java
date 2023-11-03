package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDtoIn {
    private final Long itemId;              // Идентификатор бронируемой вещи
    @NotNull
    private final LocalDateTime start;      // Дата и время начала бронирования
    @NotNull
    private final LocalDateTime end;        // Дата и время окончания бронирования

    public void hasValidTimesOrThrow() {
        LocalDateTime now = LocalDateTime.now();
        if (start == null) {
            throw new ValidationException("Определите время начала аренды.");
        }
        if (end == null) {
            throw new ValidationException("Определите время окончания аренды.");
        }
        if (end.isBefore(now)) {
            throw new ValidationException("Время окончания аренды должны быть позже текущего времени.");
        }
        if (end.isBefore(start)) {
            throw new ValidationException("Время окончания аренды должны быть позже начала аренды.");
        }
        if (start.isEqual(end)) {
            throw new ValidationException("Время начала и окончания аренды должно различаться.");
        }
        if (start.isBefore(now)) {
            throw new ValidationException("Время начала аренды должно быть позже текущего времени.");
        }
    }
}

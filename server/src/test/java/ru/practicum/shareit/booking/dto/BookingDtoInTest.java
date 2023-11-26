package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingDtoInTest {

    @Test
    void hasValidTimesOrThrow() {
        LocalDateTime start;
        LocalDateTime end;
        LocalDateTime now = LocalDateTime.now();
        ValidationException ex;

        BookingDtoIn bookingWithNullStart = new BookingDtoIn(1L, null, now);
        ex = assertThrows(ValidationException.class, () -> bookingWithNullStart.hasValidTimesOrThrow());
        assertEquals("Определите время начала аренды.", ex.getMessage());

        BookingDtoIn bookingWithNullEnd = new BookingDtoIn(1L, now, null);
        ex = assertThrows(ValidationException.class, () -> bookingWithNullEnd.hasValidTimesOrThrow());
        assertEquals("Определите время окончания аренды.", ex.getMessage());

        start = LocalDateTime.from(now).plusHours(2L);
        end = LocalDateTime.from(now).minusHours(2L);
        BookingDtoIn bookingEndBeforeNow = new BookingDtoIn(1L, start, end);
        ex = assertThrows(ValidationException.class, () -> bookingEndBeforeNow.hasValidTimesOrThrow());
        assertEquals("Время окончания аренды должны быть позже текущего времени.", ex.getMessage());

        start = LocalDateTime.from(now).plusHours(2L);
        end = LocalDateTime.from(now).plusHours(1L);
        BookingDtoIn bookingEndBeforeStart = new BookingDtoIn(1L, start, end);
        ex = assertThrows(ValidationException.class, () -> bookingEndBeforeStart.hasValidTimesOrThrow());
        assertEquals("Время окончания аренды должны быть позже начала аренды.", ex.getMessage());

        start = LocalDateTime.from(now).plusHours(1L);
        end = LocalDateTime.from(now).plusHours(1L);
        BookingDtoIn bookingStartEqualsEnd = new BookingDtoIn(1L, start, end);
        ex = assertThrows(ValidationException.class, () -> bookingStartEqualsEnd.hasValidTimesOrThrow());
        assertEquals("Время начала и окончания аренды должно различаться.", ex.getMessage());

        start = LocalDateTime.from(now);
        end = LocalDateTime.from(now).plusHours(1L);
        BookingDtoIn bookingStartEqualsNow = new BookingDtoIn(1L, start, end);
        ex = assertThrows(ValidationException.class, () -> bookingStartEqualsNow.hasValidTimesOrThrow());
        assertEquals("Время начала аренды должно быть позже текущего времени.", ex.getMessage());
    }
}
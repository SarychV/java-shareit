package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.ItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingDtoOut toBookingDtoOut(Booking booking) {
        BookingDtoOut response = new BookingDtoOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new ItemDto(
                        booking.getItem().getId(),
                        booking.getItem().getName()),
                new BookerDto(booking.getBooker().getId()));
        return response;
    }

    public static List<BookingDtoOut> toListBookingDtoOut(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOut addBookingRequest(
            @RequestBody BookingDtoIn bookingDtoIn,
            @RequestHeader(HEADER_USER_ID) @NotNull Integer bookerId) {
        return bookingService.addBooking(bookerId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut makeBookingApproved(
            @RequestHeader(HEADER_USER_ID) Integer ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingService.ownerApproveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingByOwnerOrBooker(
            @RequestHeader(HEADER_USER_ID) @NotNull Integer userId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingByOwnerOrBooker(userId, bookingId);
    }

    @GetMapping
    public List getAllBookingsByBooker(
            @RequestHeader(HEADER_USER_ID) @NotNull Integer bookerId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10000") Integer size,
            @RequestParam(required = false) String state) {
        return bookingService.getAllByBooker(bookerId, from, size, state);
    }

    @GetMapping("/owner")
    public List getAllBookingsByOwner(
            @RequestHeader(HEADER_USER_ID) @NotNull Integer ownerId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10000") Integer size,
            @RequestParam(required = false) String state) {
        return bookingService.getAllByOwner(ownerId, from, size, state);
    }
}

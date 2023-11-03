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

    private final BookingServiceImpl bookingService;

    @PostMapping
    public BookingDtoOut addBookingRequest(
            @RequestBody BookingDtoIn bookingDtoIn,
            @RequestHeader("X-Sharer-User-Id") @NotNull Integer bookerId) {
        return bookingService.addBooking(bookerId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut makeBookingApproved(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingService.ownerApproveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingByOwnerOrBooker(
            @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingByOwnerOrBooker(userId, bookingId);
    }

    @GetMapping
    public List getAllBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") @NotNull Integer bookerId,
            @RequestParam(required = false) String state) {
        return bookingService.getAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
            @RequestParam(required = false) String state) {
        return bookingService.getAllByOwner(ownerId, state);
    }
}

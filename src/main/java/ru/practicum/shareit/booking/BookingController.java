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
    private final String hEADERuSERiD = "X-Sharer-User-Id";
    
    @PostMapping

    public BookingDtoOut addBookingRequest(
            @RequestBody BookingDtoIn bookingDtoIn,
            @RequestHeader(hEADERuSERiD) @NotNull Integer bookerId) {
        return bookingService.addBooking(bookerId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut makeBookingApproved(
            @RequestHeader(hEADERuSERiD) Integer ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingService.ownerApproveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingByOwnerOrBooker(
            @RequestHeader(hEADERuSERiD) @NotNull Integer userId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingByOwnerOrBooker(userId, bookingId);
    }

    @GetMapping
    public List getAllBookingsByBooker(
            @RequestHeader(hEADERuSERiD) @NotNull Integer bookerId,
            @RequestParam(required = false) String state) {
        return bookingService.getAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List getAllBookingsByOwner(
            @RequestHeader(hEADERuSERiD) @NotNull Integer ownerId,
            @RequestParam(required = false) String state) {
        return bookingService.getAllByOwner(ownerId, state);
    }
}

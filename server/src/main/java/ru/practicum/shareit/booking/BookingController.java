package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOut addBookingRequest(
            @RequestBody BookingDtoIn bookingDtoIn,
            @RequestHeader(HEADER_USER_ID) Integer bookerId) {
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
            @RequestHeader(HEADER_USER_ID) Integer userId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingByOwnerOrBooker(userId, bookingId);
    }

    @GetMapping
    public List getBookingsPageByBooker(
            @RequestHeader(HEADER_USER_ID) Integer bookerId,
            @RequestParam Integer from,
            @RequestParam Integer size,
            @RequestParam String state) {
        return bookingService.getPageByBooker(bookerId, from, size, state);
    }

    @GetMapping("/owner")
    public List getBookingsPageByOwner(
            @RequestHeader(HEADER_USER_ID) Integer ownerId,
            @RequestParam Integer from,
            @RequestParam Integer size,
            @RequestParam String state) {
        return bookingService.getPageByOwner(ownerId, from, size, state);
    }
}

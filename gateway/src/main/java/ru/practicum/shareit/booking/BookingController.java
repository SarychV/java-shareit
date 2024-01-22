package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String HEADER_USER_ID = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(HEADER_USER_ID) @Positive Integer userId,
										   @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> makeBookingApproved(
			@RequestHeader(HEADER_USER_ID) @Positive Integer ownerId,
			@PathVariable @Positive Long bookingId,
			@RequestParam boolean approved) {
		log.info("Making booking approved with bookingId={}, ownerId={}, approved={}", bookingId, ownerId, approved);
		return bookingClient.ownerApproveBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(HEADER_USER_ID) @Positive Integer userId,
											 @PathVariable @Positive Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(
			@RequestHeader(HEADER_USER_ID) @Positive Long bookerId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
		Optional<BookingState> state = BookingState.from(stateParam);
		if (state.isEmpty()) {
			return new ResponseEntity<>(
					Map.of("error", "Unknown state: " + stateParam),
					HttpStatus.BAD_REQUEST);
		}

		log.info("Get bookings page of booker  bookerId={}, from={}, size={}, state {}",
				bookerId, from, size, stateParam);
		return bookingClient.getBookings(bookerId, state.get(), from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsByOwner(
			@RequestHeader(HEADER_USER_ID) @Positive Integer ownerId,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size,
			@RequestParam(name = "state", defaultValue = "all") String stateParam) {
		Optional<BookingState> state = BookingState.from(stateParam);
		if (state.isEmpty()) {
			return new ResponseEntity<>(
					Map.of("error", "Unknown state: " + stateParam),
					HttpStatus.BAD_REQUEST);
		}
		log.info("Getting bookings page of ownerId={}, from={}, size={}, state={}", ownerId, from, size, state);
		return bookingClient.getAllByOwner(ownerId, from, size, state.get());
	}
}

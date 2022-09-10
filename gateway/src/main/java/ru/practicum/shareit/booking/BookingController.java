package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;


import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Arrays;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addNewBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
												@RequestBody @Valid BookingRequestDto bookingRequestDto) {
		log.info("Creating booking {}, userId={}", bookingRequestDto, ownerId);
		return bookingClient.addNewBooking(ownerId, bookingRequestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> confirmRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
												 @RequestParam(name = "approved") Boolean status) {
		if (status.equals(false) || status.equals(true)) {
			return bookingClient.confirmRequest(userId, bookingId, status);
		}
		throw new IllegalArgumentException("Указан неверный статус брони");
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getInfoById(@RequestHeader("X-Sharer-User-Id") long userId,
											  @PathVariable long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getInfoById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllByState(@RequestHeader("X-Sharer-User-Id") long userId,
												@RequestParam(name = "state", defaultValue = "ALL") String state,
												@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
												@Positive @RequestParam(name = "size", defaultValue = "10") int size) {
		if (from < 0) {
			throw new ValidationException("Значение from не может быть отрицательным");
		}
		if (isBookingState(state)) {
			log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
			return bookingClient.getByState(userId, BookingState.valueOf(state), from, size);
		}
		throw new UnsupportedStatusException("Указан неверный параметр в URI");
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
													@RequestParam(name = "state", defaultValue = "ALL") String state,
													@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
													@Positive  @RequestParam(name = "size", defaultValue = "10") int size) {
		//todo
		if (from < 0) {
			throw new ValidationException("Значение from не может быть отрицательным");
		}
		if (isBookingState(state)) {
			return bookingClient.getByOwner(ownerId, BookingState.valueOf(state), from, size);
		}
		throw new UnsupportedStatusException("Указан неверный параметр в URI");
	}

	private boolean isBookingState(String state) {
		return Arrays.stream(BookingState.values()).anyMatch((s) -> s.name().equals(state));
	}
}

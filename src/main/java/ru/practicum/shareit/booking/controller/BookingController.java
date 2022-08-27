package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking addNewBooking(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestBody BookingDto bookingDto) {
        return bookingService.addNewBooking(ownerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking confirmRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                  @RequestParam(name = "approved") Boolean status) {
        if (status.equals(true)) {
            return bookingService.confirmRequest(userId, bookingId, BookingStatus.APPROVED);
        }
        if (status.equals(false)) {
            return bookingService.confirmRequest(userId, bookingId, BookingStatus.REJECTED);
        }
        throw new IllegalArgumentException("Указан неверный статус брони");
    }

    @GetMapping("/{bookingId}")
    public Booking getInfoById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable long bookingId) {
        return bookingService.getInfoById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getAllByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(name = "state", defaultValue = "ALL") String state,
                                       @RequestParam(name = "from", defaultValue = "0") int from,
                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        if (isBookingState(state)) {
            return bookingService.getByState(userId, state, from, size);
        }
        throw new UnsupportedStatusException("Указан неверный параметр в URI");
    }

    @GetMapping("/owner")
    public List<Booking> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                           @RequestParam(name = "state", defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        if (isBookingState(state)) {
            return bookingService.getByOwner(ownerId, state, from, size);
        }
        throw new UnsupportedStatusException("Указан неверный параметр в URI");
    }

    private boolean isBookingState(String state) {
        return Arrays.stream(BookingState.values()).anyMatch((s) -> s.name().equals(state));
    }
}

package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addNewBooking(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.addNewBooking(ownerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto confirmRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                             @RequestParam(name = "approved") Boolean status) {
        BookingStatus bookingStatus = BookingStatus.WAITING;
        if (status.equals(true)) {
            bookingStatus = BookingStatus.APPROVED;
        }
        if (status.equals(false)) {
            bookingStatus = BookingStatus.REJECTED;
        }
        return bookingService.confirmRequest(userId, bookingId, bookingStatus);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getInfoById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long bookingId) {
        return bookingService.getInfoById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0") int from,
                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
            return bookingService.getByState(userId, state, from, size);

    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
            return bookingService.getByOwner(ownerId, state, from, size);
    }
}
package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingResponseDto addNewBooking(long ownerId, BookingRequestDto bookingRequestDto);

    BookingResponseDto confirmRequest(long ownerId, long bookingId, BookingStatus status);

    BookingResponseDto getInfoById(long userId, long bookingId);

    List<BookingResponseDto> getByState(long userId, String state, Pageable pageable);

    List<BookingResponseDto> getByOwner(long ownerId, String state, Pageable pageable);
}

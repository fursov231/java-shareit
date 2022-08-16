package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking addNewBooking(long ownerId, BookingDto bookingDto);

    Booking confirmRequest(long ownerId, long bookingId, BookingStatus status);

    Booking getInfoById(long userId, long bookingId);

    List<Booking> getByState(long userId, String state);

    List<Booking> getByOwner(long ownerId, String state);
}

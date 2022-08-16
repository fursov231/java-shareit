package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepositoryCustom {
    List<Booking> findByOwnerIdAndEndIsBefore(Long ownerId);

    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status);

    List<Booking> findByOwnerIdAndStartIsAfter(Long ownerId);

    List<Booking> findByOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId);

    List<Booking> findAllByOwner(Long ownerId);
}

package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepositoryCustom {
    List<Booking> findByOwnerIdAndEndIsBefore(Long ownerId, Pageable pageable);

    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    List<Booking> findByOwnerIdAndStartIsAfter(Long ownerId, Pageable pageable);

    List<Booking> findByOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, Pageable pageable);

    List<Booking> findByOwnerId(long ownerId, Pageable pageable);
}

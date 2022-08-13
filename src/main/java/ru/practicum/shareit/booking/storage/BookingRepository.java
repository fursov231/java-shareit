package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findByItemId(long itemId);

    List<Booking> findByItemIdAndEndIsBefore(long itemId, LocalDateTime time, Sort sort);

    List<Booking> findByItemIdAndStartIsAfter(long itemId, LocalDateTime time, Sort sort);
}

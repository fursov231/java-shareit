package ru.practicum.shareit.booking.storage;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingRepositoryImpl implements BookingRepositoryCustom {
    private final BookingRepository bookingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public BookingRepositoryImpl(@Lazy BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> findByOwnerIdAndEndIsBefore(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId, pageable);
        return bookings.stream().filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId, pageable);
        return bookings.stream().filter(e -> e.getStatus().equals(status)).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerIdAndStartIsAfter(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId, pageable);
        return bookings.stream().filter(e -> e.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, Pageable pageable) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId, pageable);
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream().filter(e -> e.getStart().isBefore(now) && e.getEnd().isAfter(now))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Booking> findByOwnerId(long ownerId, Pageable pageable) {
        return entityManager.createNativeQuery(
                        "select b.id, b.end_date, b.start_date, b.status, b.booker_id, b.item_id " +
                                "from bookings b " +
                                "join items i on b.item_id = i.id " +
                                "where i.owner_id = :ownerId " +
                                "order by start_date desc ", Booking.class)
                .setParameter("ownerId", ownerId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}

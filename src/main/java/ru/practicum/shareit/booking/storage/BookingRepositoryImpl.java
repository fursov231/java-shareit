package ru.practicum.shareit.booking.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Comparator;
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
    public List<Booking> findByOwnerIdAndEndIsBefore(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId);
        return bookings.stream().filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStart, Comparator.reverseOrder())).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId);
        return bookings.stream().filter(e -> e.getStatus().equals(status)).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerIdAndStartIsAfter(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId);
        return bookings.stream().filter(e -> e.getStart().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStart, Comparator.reverseOrder())).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream().filter(e -> e.getStart().isBefore(now) && e.getEnd().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart, Comparator.reverseOrder())).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findAllByOwner(Long ownerId) {
        return bookingRepository.findByOwnerId(ownerId).stream()
                .sorted(Comparator.comparing(Booking::getStart, Comparator.reverseOrder())).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<Booking> findByOwnerId(long ownerId) {
        return entityManager.createNativeQuery(
                        "select b.id, b.end_date, b.start_date, b.status, b.booker_id, b.item_id " +
                                "from bookings b " +
                                "join items i on b.item_id = i.id " +
                                "where i.owner_id = :ownerId ", Booking.class)
                .setParameter("ownerId", ownerId)
                .getResultList();
    }
}

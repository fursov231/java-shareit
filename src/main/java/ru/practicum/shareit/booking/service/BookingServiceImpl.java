package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.booking.util.OffsetLimitPageable;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking addNewBooking(long userId, BookingDto bookingDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким id не существует");
        }
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isPresent()) {
            if (item.get().getOwnerId() == userId) {
                throw new NotFoundException("Бронирование данного предмета по указанному ownerId недоступно");
            }
            if (!item.get().getAvailable()) {
                throw new NotAvailableException("Данный предмет недоступен для бронирования");
            }
        } else {
            throw new NotFoundException("Предлагаемый к бронированию предмет не существует");
        }
        LocalDateTime now = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(now) || bookingDto.getEnd().isBefore(now)) {
            throw new ValidationException("Время бронирования не может быть в прошлом");
        }
        Booking booking = BookingMapper.dtoToBooking(bookingDto);
        booking.setItem(item.get());
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user.get());

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking confirmRequest(long userId, long bookingId, BookingStatus status) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Optional<Item> targetItem;
        if (booking.isPresent()) {
            if (booking.get().getStatus().equals(BookingStatus.APPROVED)) {
                throw new ValidationException("Бронь уже подтверждена");
            }
            targetItem = itemRepository.findById(booking.get().getItem().getId());
            if (targetItem.isPresent()) {
                if (targetItem.get().getOwnerId() == userId) {
                    booking.get().setStatus(status);
                    bookingRepository.save(booking.get());
                } else {
                    throw new NotFoundException("Неверный ownerId для изменения статуса данного предмета");
                }
            } else {
                throw new NotFoundException("Указанная вещь не найдена");
            }
        } else {
            throw new NotFoundException("Указанного бронирования не найдено");
        }
        return booking.get();
    }

    @Override
    public Booking getInfoById(long userId, long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Optional<Item> item = itemRepository.findById(booking.get().getItem().getId());
            if (item.get().getOwnerId() == userId || booking.get().getBooker().getId() == userId) {
                return booking.get();
            } else {
                throw new NotFoundException("Данный заказ по указанному ownerId не найден");
            }
        }
        throw new NotFoundException("Указанная бронь не найдена");
    }

    @Override
    public List<Booking> getByState(long userId, String state, int from, int size) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        List<Booking> emptyList = Collections.emptyList();
        Page<Booking> bookingsByState = new PageImpl<>(emptyList);
        switch (state) {
            case "WAITING":
            case "REJECTED":
                bookingsByState = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state),
                        OffsetLimitPageable.of(from, size));
                break;
            case "PAST":
                bookingsByState = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                        OffsetLimitPageable.of(from, size));
                break;
            case "CURRENT":
                bookingsByState = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), OffsetLimitPageable.of(from, size));
                break;
            case "FUTURE":
                bookingsByState = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(),
                        OffsetLimitPageable.of(from, size));
                break;
            case "ALL":
                bookingsByState = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, OffsetLimitPageable.of(from, size));
        }
        return bookingsByState.getContent();
    }

    @Override
    public List<Booking> getByOwner(long ownerId, String state, int from, int size) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        List<Booking> bookingsByState = bookingRepository.findByOwnerId(ownerId, OffsetLimitPageable.of(from, size));
        switch (state) {
            case "WAITING":
            case "REJECTED":
                bookingsByState = bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.valueOf(state), OffsetLimitPageable.of(from, size));
                break;
            case "PAST":
                bookingsByState = bookingRepository.findByOwnerIdAndEndIsBefore(ownerId, OffsetLimitPageable.of(from, size));
                break;
            case "CURRENT":
                bookingsByState = bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, OffsetLimitPageable.of(from, size));
                break;
            case "FUTURE":
                bookingsByState = bookingRepository.findByOwnerIdAndStartIsAfter(ownerId, OffsetLimitPageable.of(from, size));
                break;
            case "ALL":
                bookingsByState = bookingRepository.findByOwnerId(ownerId, OffsetLimitPageable.of(from, size));
        }
        return bookingsByState;
    }
}

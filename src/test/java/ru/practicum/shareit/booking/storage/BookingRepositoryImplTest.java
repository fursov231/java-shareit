package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:./db/shareit-test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryImplTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;

    @BeforeEach
    public void init() {
        user = userRepository.save(User.builder().id(1L).name("Ivan").email("ivan@mail.com").build());
        item = itemRepository.save(Item.builder().id(1L).available(true).ownerId(1L).name("phone").description("big phone").build());
    }


    @Test
    void shouldBeFoundByOwnerIdAndEndIsBefore() {
        bookingRepository.save(makeBooking(1L, LocalDateTime.of(2023, 1, 1, 1, 1), LocalDateTime.of(2023, 2, 2, 2, 2)));
        bookingRepository.save(makeBooking(2L, LocalDateTime.of(2022, 1, 1, 1, 1), LocalDateTime.of(2022, 2, 2, 2, 2)));
        int from = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Booking> list = bookingRepository.findByOwnerIdAndEndIsBefore(1L, pageRequest);

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0).getId(), 2L);
    }

    @Test
    void shouldBeFoundByOwnerIdAndStatus() {
        bookingRepository.save(makeBooking(1L, LocalDateTime.of(2023, 1, 1, 1, 1), LocalDateTime.of(2023, 2, 2, 2, 2)));
        bookingRepository.save(makeBooking(2L, LocalDateTime.of(2022, 1, 1, 1, 1), LocalDateTime.of(2022, 2, 2, 2, 2)));
        int from = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Booking> list = bookingRepository.findByOwnerIdAndStatus(1L, BookingStatus.REJECTED, pageRequest);

        Assertions.assertEquals(list.size(), 2);
        Assertions.assertEquals(list.get(0).getId(), 1L);
        Assertions.assertEquals(list.get(1).getId(), 2L);
    }

    @Test
    void shouldBeFoundByOwnerIdAndStartIsAfter() {
        bookingRepository.save(makeBooking(1L, LocalDateTime.of(2023, 1, 1, 1, 1), LocalDateTime.of(2023, 2, 2, 2, 2)));
        bookingRepository.save(makeBooking(2L, LocalDateTime.of(2022, 1, 1, 1, 1), LocalDateTime.of(2022, 2, 2, 2, 2)));
        int from = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Booking> list = bookingRepository.findByOwnerIdAndStartIsAfter(1L, pageRequest);

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0).getId(), 1L);
    }

    @Test
    void shouldBeFoundByOwnerIdAndStartIsBeforeAndEndIsAfter() {
        bookingRepository.save(makeBooking(1L, LocalDateTime.of(2023, 1, 1, 1, 1), LocalDateTime.of(2023, 2, 2, 2, 2)));
        bookingRepository.save(makeBooking(2L, LocalDateTime.of(2022, 1, 1, 1, 1), LocalDateTime.of(2024, 2, 2, 2, 2)));
        int from = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Booking> list = bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsAfter(1L, pageRequest);

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0).getId(), 2L);
    }

    @Test
    void shouldBeFoundAllByOwner() {
        int from = 0;
        int size = 10;
        bookingRepository.save(makeBooking(1L, LocalDateTime.of(2023, 1, 1, 1, 1), LocalDateTime.of(2023, 2, 2, 2, 2)));
        bookingRepository.save(makeBooking(2L, LocalDateTime.of(2022, 1, 1, 1, 1), LocalDateTime.of(2024, 2, 2, 2, 2)));
        User user2 = userRepository.save(User.builder().id(2L).name("Boris").email("boris@mail.com").build());
        Item item2 = itemRepository.save(Item.builder().id(2L).available(true).ownerId(2L).name("iPhone").description("new phone").build());
        bookingRepository.save(Booking.builder().id(3L).start(LocalDateTime.of(2022, 1, 1, 1, 1)).end(LocalDateTime.of(2022, 2, 2, 2, 2)).status(BookingStatus.APPROVED)
                .booker(user2)
                .item(item2)
                .build());

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Booking> list = bookingRepository.findByOwnerId(2L, pageRequest);

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0).getId(), 3L);
    }

    private Booking makeBooking(long id, LocalDateTime start, LocalDateTime end) {
        return Booking.builder().id(id).start(start).end(end).status(BookingStatus.REJECTED)
                .booker(user)
                .item(item)
                .build();
    }
}
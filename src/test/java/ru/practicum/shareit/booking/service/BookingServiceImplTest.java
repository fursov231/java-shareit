package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:./db/shareit-test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceImplTest {
    @Autowired
    private final EntityManager em;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ItemService itemService;

    private User user1;
    private User user2;

    @BeforeEach
    public void init() {
        user1 = userService.createUser(makeUserDto("Ivan", "ivan@mail.com"));
        user2 = userService.createUser(makeUserDto("Boris", "boris@mail.com"));
        itemService.createItem(user1.getId(), ItemDto.builder().name("phone").description("big size").available(true).build());
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder().name(name).email(email).build();
    }


    @Test
    void shouldBeAdded() {
        BookingDto bookingDto = makeBookingDto();
        bookingService.addNewBooking(user2.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query.setParameter("id", 1L).getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getStart().toString(), equalTo("2023-01-01T01:01"));
        assertThat(result.getEnd().toString(), equalTo("2024-01-01T01:01"));
        assertThat(result.getItem().getName(), equalTo("phone"));
    }


    @Test
    void shouldBeConfirmed() {
        BookingDto bookingDto = makeBookingDto();
        Booking savedBooking = bookingService.addNewBooking(user2.getId(), bookingDto);
        bookingService.confirmRequest(user1.getId(), savedBooking.getId(), BookingStatus.APPROVED);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query.setParameter("id", 1L).getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void shouldBeReturnedById() {
        BookingDto bookingDto = makeBookingDto();
        bookingService.addNewBooking(user2.getId(), bookingDto);
        Booking booking = bookingService.getInfoById(user2.getId(), 1L);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query.setParameter("id", 1L).getSingleResult();

        assertThat(result.getId(), equalTo(booking.getId()));
        assertThat(result.getItem().getName(), equalTo(booking.getItem().getName()));
    }

    @Test
    void shouldBeReturnedByState() {
        BookingDto bookingDto = makeBookingDto();
        bookingService.addNewBooking(user2.getId(), bookingDto);
        int from = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Booking> bookings = bookingService.getByState(user2.getId(), "FUTURE", pageRequest);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.status = :status", Booking.class);
        List<Booking> result = query.setParameter("status", BookingStatus.WAITING).getResultList();

        assertThat(result.get(0).getItem().getName(), equalTo(bookings.get(0).getItem().getName()));
        assertThat(result.get(0).getStatus(), equalTo(bookings.get(0).getStatus()));
    }

    @Test
    void shouldBeReturnedOwners() {
        BookingDto bookingDto = makeBookingDto();
        Booking addedBooking = bookingService.addNewBooking(user2.getId(), bookingDto);
        bookingService.confirmRequest(user1.getId(), addedBooking.getId(), BookingStatus.APPROVED);
        int from = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Booking> bookings = bookingService.getByOwner(user1.getId(), "ALL", pageRequest);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.item.ownerId = :ownerId", Booking.class);
        List<Booking> result = query.setParameter("ownerId", user1.getId()).getResultList();

        assertThat(result.get(0).getItem().getName(), equalTo(bookings.get(0).getItem().getName()));
        assertThat(result.get(0).getStatus(), equalTo(bookings.get(0).getStatus()));
    }

    private BookingDto makeBookingDto() {
        return new BookingDto(1L, LocalDateTime.of(2023, 1, 1, 1, 1), LocalDateTime.of(2024, 1, 1, 1, 1));
    }
}
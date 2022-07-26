package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }


    @Test
    void shouldBeAdded() throws Exception {
        Booking booking = makeBooking(1L);

        when(bookingService.addNewBooking(anyLong(), ArgumentMatchers.any()))
                .thenReturn(BookingMapper.toBookingResponseDto(booking));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(BookingMapper.toDto(booking)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.start", is("2023-01-01T01:01:00")));
    }

    @Test
    void shouldBeConfirmedApprove() throws Exception {
        Booking booking = makeBooking(1L);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingService.confirmRequest(anyLong(), anyLong(), any()))
                .thenReturn(BookingMapper.toBookingResponseDto(booking));

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.start", is("2023-01-01T01:01:00")));
    }

    @Test
    void shouldBeConfirmedReject() throws Exception {
        Booking booking = makeBooking(1L);
        booking.setStatus(BookingStatus.REJECTED);

        when(bookingService.confirmRequest(anyLong(), anyLong(), any()))
                .thenReturn(BookingMapper.toBookingResponseDto(booking));

        mvc.perform(patch("/bookings/1")
                        .param("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.start", is("2023-01-01T01:01:00")));
    }

    @Test
    void shouldBeThrownExceptionWhenConfirmedWithWrongParam() throws Exception {
        mvc.perform(patch("/bookings/1")
                        .param("approved", "ok")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldBeReturnedById() throws Exception {
        Booking booking = makeBooking(1L);

        when(bookingService.getInfoById(anyLong(), anyLong()))
                .thenReturn(BookingMapper.toBookingResponseDto(booking));

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.start", is("2023-01-01T01:01:00")));
    }

    @Test
    void shouldBeReturnedAllByState() throws Exception {
        Booking booking1 = makeBooking(1L);
        Booking booking2 = makeBooking(2L);

        when(bookingService.getByState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingMapper.toBookingResponseDto(booking1), BookingMapper.toBookingResponseDto(booking2)));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
    }

    @Test
    void shouldBeThrownExceptionWhenFoundAllByStateWithWrongBookingState() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldBeReturnedOwners() throws Exception {
        Booking booking1 = makeBooking(1L);
        Booking booking2 = makeBooking(2L);

        when(bookingService.getByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingMapper.toBookingResponseDto(booking1), BookingMapper.toBookingResponseDto(booking2)));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
    }

    @Test
    void shouldBeThrownExceptionWhenFoundAllByOwnerWithWrongBookingState() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private Booking makeBooking(long id) {
        return Booking.builder().id(id).start(LocalDateTime.of(2023, 1, 1, 1, 1)).end(LocalDateTime.of(2024, 1, 1, 1, 1))
                .status(BookingStatus.WAITING)
                .booker(User.builder().id(1L).name("Ivan").email("Ivan@Mail.com").build())
                .item(Item.builder().id(1L).name("phone").ownerId(1L).available(true).description("big phone").build())
                .build();
    }
}
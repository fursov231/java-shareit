package ru.practicum.shareit.booking.util;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.dto.BookingDtoWithTime;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static BookingDtoWithTime toBookingDto(Booking booking, Booking lastBooking, Booking nextBooking) {
        return new BookingDtoWithTime(
                booking.getId(),
                booking.getStatus(),
                booking.getBooker(),
                booking.getItem(),
                lastBooking,
                nextBooking
        );
    }

    public static Booking toBooking(BookingDtoWithTime bookingDtoWithTime) {
        return Booking.builder()
                .id(bookingDtoWithTime.getId())
                .status(bookingDtoWithTime.getStatus())
                .item(bookingDtoWithTime.getItem())
                .booker(bookingDtoWithTime.getBooker())
                .build();
    }

    public static Booking dtoToBooking(BookingDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static BookingDtoWithBookerId toBookingDtoWithBookerId(Booking booking) {
        return BookingDtoWithBookerId.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}

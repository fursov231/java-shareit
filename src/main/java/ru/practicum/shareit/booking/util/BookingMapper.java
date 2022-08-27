package ru.practicum.shareit.booking.util;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static Booking dtoToBooking(BookingDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getItem().getId(), booking.getStart(), booking.getEnd());
    }

    public static BookingDtoWithBookerId toBookingDtoWithBookerId(Booking booking) {
        return BookingDtoWithBookerId.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}

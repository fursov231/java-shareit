package ru.practicum.shareit.booking.util;

import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {
    public static Booking dtoToBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();
    }

    public static BookingRequestDto toDto(Booking booking) {
        return new BookingRequestDto(booking.getItem().getId(), booking.getStart(), booking.getEnd());
    }

    public static BookingDtoWithBookerId toBookingDtoWithBookerId(Booking booking) {
        return BookingDtoWithBookerId.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new UserDto.UserDtoResponse(booking.getBooker().getId()))
                .item(new ItemDto.ItemResponseDto(booking.getItem().getId(), booking.getItem().getName()))
                .build();
    }
}

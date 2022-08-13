package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoWithTime {
    private Long id;
    private BookingStatus status;
    private User booker;
    private Item item;
    private Booking lastBooking;
    private Booking nextBooking;
}

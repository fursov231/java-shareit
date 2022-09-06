package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDtoWithTime implements Comparable<ItemDtoWithTime> {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoWithBookerId lastBooking;
    private BookingDtoWithBookerId nextBooking;
    private List<CommentDto> comments;

    @Override
    public int compareTo(ItemDtoWithTime o) {
        if (o.getNextBooking() == null || o.getLastBooking() == null) {
            return -1;
        } else {
            return 0;
        }
    }
}

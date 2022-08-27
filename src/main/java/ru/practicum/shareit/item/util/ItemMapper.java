package ru.practicum.shareit.item.util;

import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .name(item.getName())
                .requestId(item.getRequestId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item dtoToItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .requestId(itemDto.getRequestId())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDtoWithTime itemToItemDtoWithTime(Item item, BookingDtoWithBookerId lastBooking, BookingDtoWithBookerId nextBooking) {
        return ItemDtoWithTime.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }
}

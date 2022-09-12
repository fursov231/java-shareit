package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithTime> getAllItemsByOwnerId(long ownerId, int from, int size);

    ItemDtoWithTime getItemById(long userId, long itemId);

    Item createItem(long ownerId, ItemDto item);

    Item updateItem(long ownerId, long itemId, ItemDto itemDto);

    void removeItem(long ownerId, long itemId);

    List<Item> searchItem(long ownerId, String text, int from, int size);

    CommentDto addNewComment(long ownerId, long itemId, CommentDto commentDto);
}

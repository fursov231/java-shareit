package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> getAllItemsByOwnerId(long ownerId);

    Optional<Item> getItemById(long itemId);

    Item createItem(long ownerId, ItemDto item);

    Item updateItem(long ownerId, long itemId, ItemDto itemDto);

    boolean removeItem(long ownerId, long itemId);

    List<Item> searchItem(long ownerId, String text);
}

package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findAllByOwnerId(long ownerId);

    Optional<Item> findById(long itemId);

    Item save(ItemDto itemDto);

    Item patch(long ownerId, long itemId, ItemDto itemDto);

    boolean deleteByItemIdAndOwnerId(long ownerId, long itemId);

    List<Item> searchItem(String text);
}

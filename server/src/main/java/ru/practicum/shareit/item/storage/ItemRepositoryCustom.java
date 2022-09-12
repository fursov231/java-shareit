package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

public interface ItemRepositoryCustom {
    Item updateById(long ownerId, long itemId, Item item);
}

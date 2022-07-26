package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;

    @Override
    public Item createItem(long ownerId, ItemDto itemDto) {
        Optional<User> owner = userStorage.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Передан неверный ownerId");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Поле available должно быть true");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Ошибка в передаче значения поля name");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Ошибка в передаче значения поля description");
        }

        itemDto.setOwnerId(owner.get().getId());
        return itemStorage.save(itemDto);
    }

    @Override
    public List<Item> getAllItemsByOwnerId(long ownerId) {
        return itemStorage.findAllByOwnerId(ownerId);
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return itemStorage.findById(itemId);
    }

    @Override
    public Item updateItem(long ownerId, long itemId, ItemDto itemDto) {
        Optional<Item> itemsOwner = itemStorage.findById(itemId);
        if (itemsOwner.isPresent()) {
            if (itemsOwner.get().getOwnerId() == ownerId) {
                return itemStorage.update(ownerId, itemId, itemDto);
            } else {
                throw new ForbiddenException("Доступ на изменение по указанному ownerId запрещен");
            }
        }
        throw new NotFoundException("Указанный item не найден");
    }

    @Override
    public boolean removeItem(long ownerId, long itemId) {
        return itemStorage.deleteByItemIdAndOwnerId(ownerId, itemId);
    }

    @Override
    public List<Item> searchItem(long ownerId, String text) {
        if (text.isBlank()) {
            return Collections.EMPTY_LIST;
        }
        return itemStorage.searchItem(text);
    }
}

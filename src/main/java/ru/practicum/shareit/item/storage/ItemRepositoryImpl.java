package ru.practicum.shareit.item.storage;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.requests.ItemRequest;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> itemStorage = new HashMap<>();

    @Override
    public List<Item> findAllByOwnerId(long ownerId) {
        return itemStorage.get(ownerId);
    }

    @Override
    public Optional<Item> findById(long itemId) {
        List<Item> allItems = findAllItems();
        for (var item : allItems) {
            if (item.getId() == itemId) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    @Override
    public Item save(ItemDto itemDto) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setId(getId());
        itemStorage.compute(item.getOwnerId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public boolean deleteByItemIdAndOwnerId(long ownerId, long itemId) {
        if (itemStorage.containsKey(ownerId)) {
            List<Item> list = itemStorage.get(ownerId);
            return list.removeIf(e -> e.getId() == itemId);
        }
        return false;
    }

    @SneakyThrows
    @Override
    public Item update(long ownerId, long itemId, ItemDto itemDto) {
        Optional<Item> item = findById(itemId);
        if (item.isPresent()) {
            BeanInfo beanInfo = Introspector.getBeanInfo(ItemDto.class);
            for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
                String propertyName = propertyDesc.getName();
                Object value = propertyDesc.getReadMethod().invoke(itemDto);
                if (value != null) {
                    if (propertyName.equals("name")) {
                        item.get().setName((String) value);
                    }
                    if (propertyName.equals("description")) {
                        item.get().setDescription((String) value);
                    }
                    if (propertyName.equals("available")) {
                        item.get().setAvailable((Boolean) value);
                    }
                    if (propertyName.equals("owner")) {
                        item.get().setOwnerId((long) value);
                    }
                    if (propertyName.equals("request")) {
                        item.get().setRequest((ItemRequest) value);
                    }
                }
            }
            return item.get();
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> allItems = findAllItems();
        String formattedText = text.toLowerCase();
        return allItems
                .stream()
                .filter(e -> e.getName().toLowerCase().contains(formattedText)
                        || e.getDescription().toLowerCase().contains(formattedText)
                        && e.getAvailable())
                .collect(Collectors.toList());
    }

    private long getId() {
        long lastId = itemStorage.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

    private List<Item> findAllItems() {
        List<Item> allItems = new ArrayList<>();
        for (var list : itemStorage.values()) {
            allItems.addAll(list);
        }
        return allItems;
    }
}

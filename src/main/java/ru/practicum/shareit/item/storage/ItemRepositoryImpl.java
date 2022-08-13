package ru.practicum.shareit.item.storage;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Optional;

public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final ItemRepository itemRepository;

    public ItemRepositoryImpl(@Lazy ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @SneakyThrows
    @Override
    public Item updateById(long ownerId, long itemId, Item item) {
        Optional<Item> targetItem = itemRepository.findById(itemId);
        if (targetItem.isPresent()) {
            if (targetItem.get().getOwnerId() == ownerId) {
                BeanInfo beanInfo = Introspector.getBeanInfo(Item.class);
                for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
                    String propertyName = propertyDesc.getName();
                    Object value = propertyDesc.getReadMethod().invoke(item);
                    if (value != null) {
                        if (propertyName.equals("name")) {
                            targetItem.get().setName((String) value);
                        }
                        if (propertyName.equals("description")) {
                            targetItem.get().setDescription((String) value);
                        }
                        if (propertyName.equals("available")) {
                            targetItem.get().setAvailable((Boolean) value);
                        }
                        if (propertyName.equals("owner")) {
                            targetItem.get().setOwnerId((Long) value);
                        }
                        if (propertyName.equals("request")) {
                            targetItem.get().setRequestId((Long) value);
                        }
                    }
                }
                return targetItem.get();
            } else {
                throw new ForbiddenException("Доступ на изменение по указанному ownerId запрещен");
            }
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
    }
}

package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:./db/shareit-test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;


    @Test
    void shouldBeSavedItem() {
        Item item = makeItem(1, "device");
        Item savedItem = itemRepository.save(item);
        Assertions.assertEquals(item, savedItem);
    }

    @Test
    void shouldBeSearchedByText() {
        Item item = makeItem(1, "device");
        String text = "analytics";
        itemRepository.save(item);
        int from = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Item> items = itemRepository.search(text, pageRequest);

        Assertions.assertEquals(items.get(0).getId(), item.getId());
    }

    @Test
    void shouldBeFoundAllByRequestId() {
        Item item1 = itemRepository.save(makeItem(1, "device1"));
        Item item2 = itemRepository.save(makeItem(2, "device2"));

        List<Item> itemsByRequestId = itemRepository.findAllByRequestId(1);

        Assertions.assertEquals(itemsByRequestId.get(0).getId(), item1.getId());
        Assertions.assertEquals(itemsByRequestId.get(1).getId(), item2.getId());
    }

    @Test
    void shouldBeUpdatedById() {
        Item item1 = itemRepository.save(makeItem(1, "device1"));
        Item item2 = itemRepository.save(makeItem(2, "updatedDevice"));

        Item updatedItem = itemRepository.updateById(1, 1, item2);

        Assertions.assertEquals(updatedItem.getName(), item2.getName());
    }

    @Test
    void shouldBeThrownExceptionWhenUpdatedByIdWithWrongOwnerId() {
        Item item1 = itemRepository.save(makeItem(1, "device1"));
        Item item2 = itemRepository.save(makeItem(2, "updatedItem"));

        final ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> itemRepository.updateById(2, 1, item2));

        Assertions.assertEquals(exception.getMessage(), "Доступ на изменение по указанному ownerId запрещен");
    }

    private Item makeItem(long id, String name) {
        return Item.builder().id(id).name(name).description("for analytics").requestId(1L).ownerId(1L).available(true).build();
    }
}
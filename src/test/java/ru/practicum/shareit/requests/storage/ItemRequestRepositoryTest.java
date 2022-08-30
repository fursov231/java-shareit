package ru.practicum.shareit.requests.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:./db/shareit-test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;


    @Test
    void shouldBeFoundAllByRequestor() {
        ItemRequest itemRequest1 = itemRequestRepository.save(ItemRequest.builder().id(1L).description("phone1").requestor(1L).created(LocalDateTime.now()).build());
        ItemRequest itemRequest2 = itemRequestRepository.save(ItemRequest.builder().id(2L).description("phone2").requestor(2L).created(LocalDateTime.now()).build());

        List<ItemRequest> list = itemRequestRepository.findAllByRequestor(2L);

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0).getDescription(), itemRequest2.getDescription());
    }
}
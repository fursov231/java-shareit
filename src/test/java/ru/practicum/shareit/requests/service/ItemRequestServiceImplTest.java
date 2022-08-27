package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.util.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:./db/shareit-test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestServiceImplTest {
    @Autowired
    private final EntityManager em;

    @Autowired
    private final ItemRequestService itemRequestService;

    @Autowired
    private final UserService userService;

    private User user;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    public void init() {
        user = userService.createUser(UserDto.builder().name("Ivan").email("ivan@mail.com").build());
        user2 = userService.createUser(UserDto.builder().name("Boris").email("boris@mail.com").build());
        itemRequest1 = makeItemRequest(1L);
        itemRequest2 = makeItemRequest(2L);
    }


    @Test
    void shouldBeSavedNewRequest() {
        itemRequestService.saveNewRequest(user.getId(), ItemRequestMapper.toItemRequestDto(itemRequest1));

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir " +
                "where ir.description = :description", ItemRequest.class);
        ItemRequest result = query.setParameter("description", itemRequest1.getDescription())
                .getSingleResult();

        assertThat(result.getId(), equalTo(itemRequest1.getId()));
        assertThat(result.getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(result.getRequestor(), equalTo(itemRequest1.getRequestor()));
    }


    @Test
    void shouldBeReturnedRequestsListByOwnerId() {
        itemRequestService.saveNewRequest(user.getId(), ItemRequestMapper.toItemRequestDto(itemRequest1));
        itemRequestService.saveNewRequest(user.getId(), ItemRequestMapper.toItemRequestDto(itemRequest2));

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir " +
                "where ir.requestor = :ownerId", ItemRequest.class);
        List<ItemRequest> result = query.setParameter("ownerId", user.getId())
                .getResultList();

        assertThat(result.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(result.get(1).getId(), equalTo(itemRequest2.getId()));
    }

    @Test
    void shouldBeReturnedById() {
        itemRequestService.saveNewRequest(user.getId(), ItemRequestMapper.toItemRequestDto(itemRequest1));
        itemRequestService.saveNewRequest(user.getId(), ItemRequestMapper.toItemRequestDto(itemRequest2));
        ItemRequestDto savedItemRequests = itemRequestService.getRequestById(user.getId(), itemRequest2.getId());

        assertThat(savedItemRequests.getId(), equalTo(itemRequest2.getId()));
    }

    @Test
    void shouldBeReturnedAllRequests() {
        itemRequestService.saveNewRequest(user.getId(), ItemRequestMapper.toItemRequestDto(itemRequest1));
        itemRequestService.saveNewRequest(user.getId(), ItemRequestMapper.toItemRequestDto(itemRequest2));

        List<ItemRequestDto> savedItemRequests = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertThat(savedItemRequests.size(), equalTo(2));
    }


    private ItemRequest makeItemRequest(Long id) {
        return ItemRequest.builder().id(id).description("iphone").created(LocalDateTime.now()).requestor(1L).build();
    }
}
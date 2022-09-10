package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:./db/shareit-test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceImplTest {

    @Autowired
    private final EntityManager em;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final BookingService bookingService;

    private UserResponseDto user1;
    private UserResponseDto user2;

    @BeforeAll
    public void init() {
        user1 = userService.createUser(makeUserDto("Ivan", "ivan@mail.com"));
        user2 = userService.createUser(makeUserDto("Boris", "boris@mail.com"));
    }


    @Test
    void test1_shouldBeCreated() {
        ItemDto itemDto = makeItemDto("device", "analytics tool", 1, true);

        itemService.createItem(user1.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getOwnerId(), equalTo(user1.getId()));
        assertThat(item.getRequestId(), equalTo(itemDto.getRequestId()));
    }

    @Test
    void test2_shouldNotBeCreatedWithoutAvailable() {
        ItemDto itemDto = mock(ItemDto.class);
        when(itemDto.getAvailable()).thenReturn(null);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(user1.getId(), itemDto));

        Assertions.assertEquals(exception.getMessage(), "Поле available должно быть true");
    }

    @Test
    void test3_shouldNotBeCreatedWithEmptyName() {
        ItemDto itemDto = mock(ItemDto.class);
        when(itemDto.getName()).thenReturn("");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(user1.getId(), itemDto));

        Assertions.assertEquals(exception.getMessage(), "Ошибка в передаче значения поля name");
    }

    @Test
    void test4_shouldNotBeCreatedWithoutName() {
        ItemDto itemDto = mock(ItemDto.class);
        when(itemDto.getName()).thenReturn(null);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(user1.getId(), itemDto));

        Assertions.assertEquals(exception.getMessage(), "Ошибка в передаче значения поля name");
    }

    @Test
    void test5_shouldNotBeCreatedWithoutDescription() {
        ItemDto itemDto = mock(ItemDto.class);
        when(itemDto.getName()).thenReturn("Ivan");
        when(itemDto.getDescription()).thenReturn(null);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(user1.getId(), itemDto));

        Assertions.assertEquals(exception.getMessage(), "Ошибка в передаче значения поля description");
    }

    @Test
    void test6_shouldNotBeCreatedWithEmptyDescription() {
        ItemDto itemDto = mock(ItemDto.class);
        when(itemDto.getName()).thenReturn("Ivan");
        when(itemDto.getDescription()).thenReturn("");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(user1.getId(), itemDto));

        Assertions.assertEquals(exception.getMessage(), "Ошибка в передаче значения поля description");
    }

    @Test
    void test7_shouldBeFoundAllItemsByOwnerId() {
        itemService.createItem(user1.getId(), makeItemDto("device1", "analytics tool", 1, true));
        itemService.createItem(user1.getId(), makeItemDto("device2", "analytics tool", 2, true));

        assertThat((itemService.getAllItemsByOwnerId(user1.getId(), 0, 10).size()), equalTo(2));
    }

    @Test
    void test8_shouldNotBeAllItemsFoundByWrongUserId() {
        itemService.createItem(user1.getId(), makeItemDto("device1", "analytics tool", 1, true));
        itemService.createItem(user1.getId(), makeItemDto("device2", "analytics tool", 2, true));

        assertThat((itemService.getAllItemsByOwnerId(user2.getId(), 0, 10).size()), equalTo(0));
    }

    @Test
    void test10_shouldBeFoundById() {
        Item item = itemService.createItem(user1.getId(), makeItemDto("device1", "analytics tool", 1, true));

        assertThat((itemService.getItemById(user1.getId(), item.getId()).getName()), equalTo(item.getName()));
    }

    @Test
    void test11_shouldBeThrownExceptionWhenRequestingNonExistentItem() {
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(user1.getId(), 99));

        Assertions.assertEquals(exception.getMessage(), "Предмет не найден");
    }

    @Test
    void test12_shouldBeUpdated() {
        Item item = itemService.createItem(user1.getId(), makeItemDto("device1", "analytics tool", 1, true));
        ItemDto updatedItemDto = makeItemDto("new device", "analytics tool", 1, true);


        assertThat((itemService.updateItem(user1.getId(), item.getId(), updatedItemDto).getName()),
                equalTo(updatedItemDto.getName()));
    }

    @Test
    void test13_shouldBeRemoved() {
        Item item = itemService.createItem(user1.getId(), makeItemDto("device1", "analytics tool", 1, true));

        assertThat((itemService.getItemById(user1.getId(), item.getId()).getName()), equalTo(item.getName()));

        itemService.removeItem(user1.getId(), item.getId());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(user1.getId(), item.getId()));
        Assertions.assertEquals(exception.getMessage(), "Предмет не найден");
    }

    @Test
    void test14_searchItemShouldBeFoundByText() {
        Item item = itemService.createItem(user1.getId(), makeItemDto("device", "analytics tool", 1, true));
        Item item2 = itemService.createItem(user1.getId(), makeItemDto("telePhone", "mobile", 2, true));

        assertThat((itemService.searchItem(user1.getId(), "telephone", 0, 10).get(0).getName()), equalTo(item2.getName()));
        assertThat((itemService.searchItem(user1.getId(), "telephone", 0, 10).size()), equalTo(1));
    }

    @Test
    void test15_searchItemShouldBeReturnEmptyListWithBlankText() {
        itemService.createItem(user1.getId(), makeItemDto("device", "analytics tool", 1, true));

        assertThat((itemService.searchItem(user1.getId(), "", 0, 10).size()), equalTo(0));
    }

    @Test
    void test16_shouldBeAddedNewComment() throws InterruptedException {
        Item item = itemService.createItem(user1.getId(), makeItemDto("device", "analytics tool", 1, true));
        BookingRequestDto bookingRequestDto = new BookingRequestDto(item.getId(),
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2));
        BookingResponseDto booking = bookingService.addNewBooking(user2.getId(), bookingRequestDto);
        bookingService.confirmRequest(user1.getId(), booking.getId(), BookingStatus.APPROVED);
        CommentDto commentDto = CommentDto.builder().text("looks nice").build();
        Thread.sleep(2000);
        CommentDto result = itemService.addNewComment(user2.getId(), item.getId(), commentDto);

        assertThat(result.getText(), equalTo(commentDto.getText()));
    }

    @Test
    void test17_shouldBeThrownExceptionWhenAddedNewCommentByWrongUserId() throws InterruptedException {
        Item item = itemService.createItem(user1.getId(), makeItemDto("device", "analytics tool", 1, true));
        CommentDto commentDto = CommentDto.builder().text("looks nice").build();
        Thread.sleep(2000);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addNewComment(user1.getId(), item.getId(), commentDto));
        Assertions.assertEquals(exception.getMessage(), "Передан неверный userId или itemId для добавления отзыва");
    }

    @Test
    void test18_shouldBeThrownExceptionWhenAddedNewCommentForNonExistingItem() {
        CommentDto commentDto = CommentDto.builder().text("looks nice").build();
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addNewComment(user1.getId(), 99, commentDto));
        Assertions.assertEquals(exception.getMessage(), "Предмет или пользователь не найден");
    }


    private ItemDto makeItemDto(String name, String description, long requestId, boolean available) {
        return ItemDto.builder().name(name).description(description).requestId(requestId).available(available).build();
    }

    private UserRequestDto makeUserDto(String name, String email) {
        return UserRequestDto.builder().name(name).email(email).build();
    }
}
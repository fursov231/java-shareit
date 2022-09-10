package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }


    @Test
    void shouldBeReturnAllItemsByOwner() throws Exception {
        //given
        ItemDtoWithTime itemDtoWithTime = makeItemDtoWithTime();
        //when
        when(itemService.getAllItemsByOwnerId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDtoWithTime));
        //then
        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithTime.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithTime.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithTime.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(1)))
                .andExpect(jsonPath("$[0].nextBooking.id", is(2)))
                .andExpect(jsonPath("$[0].comments[0].text", is(itemDtoWithTime.getComments().get(0).getText())));
    }

    @Test
    void shouldBeSavedNewItem() throws Exception {
        //given
        Item item = makeItem();
        //when
        when(itemService.createItem(anyLong(), ArgumentMatchers.any()))
                .thenReturn(item);
        //then
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(ItemMapper.toItemDto(item)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(1)));
    }

    @Test
    void shouldBeReturnedItemById() throws Exception {
        ItemDtoWithTime itemDtoWithTime = makeItemDtoWithTime();

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoWithTime);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDtoWithTime.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithTime.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithTime.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(1)))
                .andExpect(jsonPath("$.nextBooking.id", is(2)))
                .andExpect(jsonPath("$.comments[0].text", is(itemDtoWithTime.getComments().get(0).getText())));
    }

    @Test
    void shouldBeAddedNewComment() throws Exception {
        CommentDto commentDto = makeCommentDto();

        when(itemService.addNewComment(anyLong(), anyLong(), ArgumentMatchers.any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void shouldBePatched() throws Exception {
        Item item = makeItem();

        when(itemService.updateItem(anyLong(), anyLong(), ArgumentMatchers.any()))
                .thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(ItemMapper.toItemDto(item)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void shouldBeRemoved() throws Exception {
        doNothing().when(itemService).removeItem(anyLong(), anyLong());

        mvc.perform(delete("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(itemService, times(1)).removeItem(anyLong(), anyLong());
    }

    @Test
    void shouldBeSearchedItem() throws Exception {
        Item item = makeItem();

        when(itemService.searchItem(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(item));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "device")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));
    }

    private ItemDtoWithTime makeItemDtoWithTime() {
        Item item = makeItem();
        BookingDtoWithBookerId bookingDtoWithBookerId1 = BookingDtoWithBookerId.builder().bookerId(item.getOwnerId()).id(1L).build();
        BookingDtoWithBookerId bookingDtoWithBookerId2 = BookingDtoWithBookerId.builder().bookerId(item.getOwnerId()).id(2L).build();
        CommentDto commentDto = CommentDto.builder().id(1L).authorName("Ivan").created(LocalDateTime.now()).text("looks nice").build();
        return ItemDtoWithTime.builder().id(1L).name(item.getName()).description(item.getDescription())
                .available(item.getAvailable()).lastBooking(bookingDtoWithBookerId1)
                .nextBooking(bookingDtoWithBookerId2).comments(List.of(commentDto)).build();
    }

    private Item makeItem() {
        return Item.builder().id(1L).name("device").description("for analytics").requestId(1L).ownerId(1L).available(true).build();
    }

    private CommentDto makeCommentDto() {
        return CommentDto.builder().id(1L).created(LocalDateTime.now()).authorName("Ivan").text("looks nice").build();
    }
}

package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoWithTimeTest {

    @Autowired
    private JacksonTester<ItemDtoWithTime> json;


    @Test
    void testItemDto() throws Exception {
        ItemDtoWithTime itemDtoWithTime = makeItemDtoWithTime();

        JsonContent<ItemDtoWithTime> result = json.write(itemDtoWithTime);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("device");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("for analytics");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("looks nice");
    }

    private ItemDtoWithTime makeItemDtoWithTime() {
        Item item = Item.builder().id(1L).name("device").description("for analytics").requestId(1L).ownerId(1L).available(true).build();
        BookingDtoWithBookerId bookingDtoWithBookerId1 = BookingDtoWithBookerId.builder().bookerId(item.getOwnerId()).id(1L).build();
        BookingDtoWithBookerId bookingDtoWithBookerId2 = BookingDtoWithBookerId.builder().bookerId(item.getOwnerId()).id(2L).build();
        CommentDto commentDto = CommentDto.builder().id(1L).authorName("Ivan").created(LocalDateTime.now()).text("looks nice").build();
        return ItemDtoWithTime.builder().id(1L).name(item.getName()).description(item.getDescription())
                .available(item.getAvailable()).lastBooking(bookingDtoWithBookerId1)
                .nextBooking(bookingDtoWithBookerId2).comments(List.of(commentDto)).build();
    }
}
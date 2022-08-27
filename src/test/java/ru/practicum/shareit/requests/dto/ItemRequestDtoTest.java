package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;


    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = makeItemRequestDto();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("phone");
        assertThat(result).extractingJsonPathBooleanValue("$.items.[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].description").isEqualTo("iphone");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2020-01-01T01:01:00");
    }

    private ItemRequestDto makeItemRequestDto() {
        return ItemRequestDto.builder().id(1L).description("phone").items(List.of(Item.builder().id(1L).requestId(1L).available(true).description("iphone").build()))
                .created(LocalDateTime.of(2020, 1, 1, 1, 1)).build();
    }
}
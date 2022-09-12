package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserRequestDtoTest {
    @Autowired
    private JacksonTester<UserRequestDto> json;


    @Test
    void testUserDto() throws Exception {
        UserRequestDto userRequestDto = makeUserDto();

        JsonContent<UserRequestDto> result = json.write(userRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userRequestDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userRequestDto.getEmail());
    }

    private UserRequestDto makeUserDto() {
        return UserRequestDto.builder().name("Ivan").email("ivan@mail.com").build();
    }
}
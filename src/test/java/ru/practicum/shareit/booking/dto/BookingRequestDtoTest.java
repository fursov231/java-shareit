package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingRequestDtoTest {
    @Autowired
    private JacksonTester<BookingRequestDto> json;


    @Test
    void testBookingDto() throws Exception {
        BookingRequestDto bookingRequestDto = makeBookingDto();

        JsonContent<BookingRequestDto> result = json.write(bookingRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-02-02T02:02:00");
    }

    private BookingRequestDto makeBookingDto() {
        return new BookingRequestDto(1L, LocalDateTime.of(2022, 1, 1, 1, 1), LocalDateTime.of(2022, 2, 2, 2, 2));
    }

}
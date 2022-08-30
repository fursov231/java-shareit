package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private String name;
    private String description;
    private long requestId;
    private Boolean available;

    @Data
    @AllArgsConstructor
    public static class ItemResponseDto {
        private Long id;
        private String name;
    }

}

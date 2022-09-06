package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import java.time.LocalDateTime;
import java.util.List;


@Data
@RequiredArgsConstructor
@Builder
public class ItemRequestDto {
    private long id;
    private final String description;
    private List<ItemDto> items;
    private LocalDateTime created;

    public ItemRequestDto(long id, String description, List<ItemDto> items, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.items = items;
        this.created = created;
    }
}

package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest saveNewRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequestsByOwnerId(long ownerId);

    List<ItemRequestDto> getAllRequests(long userId, int from, int size);

    ItemRequestDto getRequestById(long userId, long requestId);
}

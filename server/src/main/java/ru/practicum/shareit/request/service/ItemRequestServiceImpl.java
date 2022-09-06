package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.util.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequest saveNewRequest(long ownerId, ItemRequestDto itemRequestDto) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Передан неверный ownerId");
        }
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Ошибка в передаче значения поля description");
        }

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(ownerId);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequestsByOwnerId(long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Передан неверный ownerId");
        }
        List<ItemRequest> requestsList = itemRequestRepository.findAllByRequestor(ownerId);
        List<ItemRequestDto> result = new ArrayList<>();

        for (var request : requestsList) {
            List<Item> items = itemRepository.findAllByRequestId(request.getId());
            result.add(new ItemRequestDto(request.getId(), request.getDescription(), items, request.getCreated()));
        }
        return result;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Передан неверный userId");
        }
        Pageable paging = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> pagedResult = itemRequestRepository.findAll(paging);
        List<ItemRequest> requests = new ArrayList<>();
        if (pagedResult.hasContent()) {
            requests = new ArrayList<>(pagedResult.getContent());
        }
        List<ItemRequest> operatedList = new ArrayList<>();
        requests.stream()
                .filter(e -> e.getRequestor() == userId)
                .forEach(operatedList::add);
        requests.removeAll(operatedList);

        List<ItemRequestDto> result = new ArrayList<>();
        if (!requests.isEmpty()) {
            for (var request : requests) {
                List<Item> items = itemRepository.findAllByRequestId(request.getId());
                result.add(new ItemRequestDto(request.getId(), request.getDescription(), items, request.getCreated()));
            }
        }
        return result;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Передан неверный userId");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), items, itemRequest.getCreated());
    }
}



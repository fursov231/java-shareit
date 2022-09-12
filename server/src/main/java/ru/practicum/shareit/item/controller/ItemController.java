package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithTime> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestParam(name = "from", defaultValue = "0") int from,
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemService.getAllItemsByOwnerId(ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                    @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        return itemService.addNewComment(ownerId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithTime getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    public Item saveNewItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestBody ItemDto itemDto) {
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long itemId) {
        itemService.removeItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestParam String text,
                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemService.searchItem(ownerId, text, from, size);
    }
}
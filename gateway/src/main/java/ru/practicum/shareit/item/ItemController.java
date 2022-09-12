package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemClient.getAllItemsByOwnerId(ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                @PathVariable long itemId,
                                                @RequestBody CommentDto commentDto) {
        return itemClient.addNewComment(ownerId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @RequestBody ItemDto itemDto) {
        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @PathVariable long itemId,
                                            @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                           @PathVariable long itemId) {
        itemClient.removeItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestParam String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemClient.searchItem(ownerId, text, from, size);
    }
}

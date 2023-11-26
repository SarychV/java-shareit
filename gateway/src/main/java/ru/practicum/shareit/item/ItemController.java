package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;


@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER_USER_ID) Integer ownerId,
                                             @RequestBody @Valid ItemWithRequestIdDto itemDto) {
        return itemClient.addItem(itemDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(
            @RequestHeader(HEADER_USER_ID) @Positive Integer userId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Adding comment to item with userId={}, itemId={}, commentDto={}", userId, itemId, commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(HEADER_USER_ID) @Positive Integer ownerId,
            @RequestBody @Valid ItemDto itemDto,
            @PathVariable @Positive Long itemId) {
        log.info("Updating item with itemId={}, itemDto={}, ownerId={}", itemId, itemDto, ownerId);
        return itemClient.updateItem(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(HEADER_USER_ID) @Positive Integer userId,
            @PathVariable @Positive Long itemId) {
        log.info("Getting item with itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerId(
            @RequestHeader(HEADER_USER_ID) @Positive Integer ownerId) {
        log.info("Getting all items by ownerId={}", ownerId);
        return itemClient.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> lookupItems(
            @RequestParam String text) {
        log.info("Search items by text={}", text);
        return itemClient.lookupItemsByText(text);
    }
}


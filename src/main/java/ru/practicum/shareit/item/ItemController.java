package ru.practicum.shareit.item;

/**
 * TODO Sprint add-controllers.
 */

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String hEADERuSERiD = "X-Sharer-User-Id";
    
    @PostMapping
    public ItemDto createItem(@RequestHeader(hEADERuSERiD) @NotNull Integer ownerId,
                              @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(
            @RequestHeader(hEADERuSERiD) @NotNull Integer userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(hEADERuSERiD) @NotNull Integer ownerId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return itemService.updateItem(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoExtended getItem(@RequestHeader(hEADERuSERiD) @NotNull Integer userId,
                                   @PathVariable Long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List getAllItemsByOwnerId(
            @RequestHeader(hEADERuSERiD) @NotNull Integer ownerId) {
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List lookupItems(@RequestParam String text) {
        return itemService.lookupItemsByText(text);
    }
}


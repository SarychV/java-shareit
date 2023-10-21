package ru.practicum.shareit.item;

/**
 * TODO Sprint add-controllers.
 */

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.util.List;



@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                           @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                           @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return itemService.updateItem(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                        @PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List getAllItemsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId) {
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List lookupItems(@RequestParam String text) {
        return itemService.lookupItemsByText(text);
    }
}


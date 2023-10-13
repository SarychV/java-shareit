package ru.practicum.shareit.item;

/**
 * TODO Sprint add-controllers.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;
import java.util.List;



@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    ItemService itemService;

    @PostMapping
    public Item createItem(@RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                           @RequestBody ItemDto itemDto) {
        itemDto.setOwner(ownerId);
        return itemService.addItem(itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                           @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        itemDto.setOwner(ownerId);
        return itemService.updateItem(itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                        @PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<Item> getAllItemsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId) {
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<Item> lookupItems(@RequestParam String text) {
        return itemService.lookupItemsByText(text);
    }
}


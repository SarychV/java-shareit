package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto item);

    Item updateItem(Long id, ItemDto item);

    Item getItem(Long itemId);

    List<Item> getAllItemsByOwnerId(Integer ownerId);

    List<Item> lookupItemsByText(String text);
}

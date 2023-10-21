package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto item, Integer ownerId);

    ItemDto updateItem(Long id, ItemDto item, Integer ownerId);

    ItemDto getItem(Long itemId);

    List getAllItemsByOwnerId(Integer ownerId);

    List lookupItemsByText(String text);
}

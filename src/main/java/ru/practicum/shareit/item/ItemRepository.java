package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(Long id);

    List<Item> getAllItemsByOwnerId(Integer ownerId);

    List<Item> lookupItemsByText(String text);
}

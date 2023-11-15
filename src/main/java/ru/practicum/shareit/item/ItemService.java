package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;

import java.util.List;

public interface ItemService {
    ItemWithRequestIdDto addItem(ItemWithRequestIdDto item, Integer ownerId);

    ItemDto updateItem(Long id, ItemDto item, Integer ownerId);

    ItemDtoExtended getItem(Long itemId, Integer ownerId);

    List getAllItemsByOwnerId(Integer ownerId);

    List lookupItemsByText(String text);

    CommentDto addComment(Integer commenterId, Long itemId, CommentDto commentDto);
}

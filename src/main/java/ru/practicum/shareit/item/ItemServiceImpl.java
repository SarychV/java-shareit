package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer ownerId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(ownerId);

        if (item.getName().isEmpty()) {
            throw new ValidationException("Поле name должно иметь значение.");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("Поле description не должно быть null.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Поле available не должно быть null.");
        }
        if (userRepository.getUser(ownerId) == null) {
            throw new NotFoundException(
                    String.format("Не найден владелец с идентификатором: %d.", ownerId));
        }
        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto, Integer ownerId) {
        Item modifiedItem = Item.copyOf(itemRepository.getItem(id));

        if (userRepository.getUser(ownerId) == null) {
            throw new NotFoundException(
                    String.format("Не найден владелец с id=%d.", ownerId));
        } else {
            if (!modifiedItem.getOwner().equals(ownerId)) {
                throw new NotFoundException(
                        String.format("Пользователь с id=%d не владелец вещи с id=%d.",
                                ownerId, id));
            }
        }

        String name = itemDto.getName();
        if (name != null) {
            modifiedItem.setName(name);
        }

        String description = itemDto.getDescription();
        if (description != null) {
            modifiedItem.setDescription(description);
        }

        Boolean available = itemDto.getAvailable();
        if (available != null) {
            modifiedItem.setAvailable(available);
        }

        return ItemMapper.toItemDto(itemRepository.updateItem(modifiedItem));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public List getAllItemsByOwnerId(Integer ownerId) {
        return itemRepository.getAllItemsByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List lookupItemsByText(String text) {
        return itemRepository.lookupItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

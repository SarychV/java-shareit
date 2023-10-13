package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public Item addItem(ItemDto itemDto) {
        Item item = null;
        if (itemDto.getName().isEmpty()) {
            throw new ValidationException("Поле name должно иметь значение.");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException("Поле description не должно быть null.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Поле available не должно быть null.");
        }
        if (userRepository.getUser(itemDto.getOwner()) != null) {
            item = itemRepository.addItem(ItemMapper.toItem(itemDto));
        }
        return item;
    }

    @Override
    public Item updateItem(Long id, ItemDto itemDto) {
        Item item = Item.copyOf(itemRepository.getItem(id));
        if (userRepository.getUser(itemDto.getOwner()) != null) {
            if (!Objects.equals(itemDto.getOwner(), item.getOwner())) {
                throw new NotFoundException("Неверно указан владелец.");
            }
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemRepository.updateItem(id, item);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<Item> getAllItemsByOwnerId(Integer ownerId) {
        return itemRepository.getAllItemsByOwnerId(ownerId);
    }

    @Override
    public List<Item> lookupItemsByText(String text) {
        return itemRepository.lookupItemsByText(text);
    }
}

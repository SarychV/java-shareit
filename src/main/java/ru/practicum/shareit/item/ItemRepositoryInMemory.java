package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long lastItemId = 0L;

    @Override
    public Item addItem(Item item) {
        Long newId = ++lastItemId;
        item.setId(newId);
        items.put(newId, item);
        return items.get(newId);
    }

    @Override
    public Item updateItem(Item item) {
        Long id = item.getId();
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public Item getItem(Long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException(String.format("Вещь с id=%d отсутствует в базе.", id));
        }
        return item;
    }

    @Override
    public List<Item> getAllItemsByOwnerId(Integer ownerId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> lookupItemsByText(String text) {
        return items.values().stream()
                .filter(item -> {
                    String searchText = text.toLowerCase();
                    if (!searchText.isBlank()
                        && (item.getName().toLowerCase().contains(searchText)
                            || item.getDescription().toLowerCase().contains((searchText)))
                        && item.getAvailable()) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
    }
}

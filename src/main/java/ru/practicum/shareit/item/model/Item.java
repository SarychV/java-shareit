package ru.practicum.shareit.item.model;
/**
 * TODO Sprint add-controllers.
 */

import lombok.Data;


@Data
public class Item {
    private Long id;                // Уникальный идентификатор вещи
    private String name;            // Название вещи
    private String description;     // Развернутое описание
    private Boolean available;      // Доступна вещь для аренды или нет
    private Integer owner;          // Владелец вещи
    private Long requestId;         // Если вещь была создана по запросу другого пользователя, то в поле
                                    // будет храниться ссылка на запрос, иначе null

    public static Item copyOf(Item original) {
        Item copy = new Item();
        copy.id = original.id;
        copy.name = original.name;
        copy.description = original.description;
        copy.available = original.available;
        copy.owner = original.owner;
        copy.requestId = original.requestId;
        return copy;
    }
}

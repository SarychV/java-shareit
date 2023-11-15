package ru.practicum.shareit.item.model;
/**
 * TODO Sprint add-controllers.
 */

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // Уникальный идентификатор вещи

    @Column
    private String name;            // Название вещи

    @Column
    private String description;     // Развернутое описание

    @Column(name = "is_available")
    private Boolean available;      // Доступна вещь для аренды или нет

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;             // Владелец вещи

    @Column(name = "request_id")
    private Long requestId;    // Если вещь была создана по запросу другого пользователя, то в поле
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



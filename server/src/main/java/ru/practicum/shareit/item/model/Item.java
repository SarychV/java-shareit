package ru.practicum.shareit.item.model;

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
    private Long requestId;         // Если вещь была создана по запросу другого пользователя, то в поле
                                    // будет храниться ссылка на запрос, иначе null
}



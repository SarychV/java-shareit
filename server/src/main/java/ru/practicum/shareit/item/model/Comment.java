package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // уникальный идентификатор комментария

    private String text;            // Текст комментария

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;              // Вещь, которая комментируется

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User user;              // Пользователь, оставивший комментарий

    @Column(name = "create_date")
    private LocalDateTime created;  // Дата создания комментария
}

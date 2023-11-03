package ru.practicum.shareit.booking.model;

import lombok.Data;
/**
 * TODO Sprint add-bookings.
 */

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // Уникальный идентификатор бронирования

    @Column(name = "start_date")
    private LocalDateTime start;    // Дата и время начала бронирования

    @Column(name = "end_date")
    private LocalDateTime end;      // Дата и время окончания бронирования

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;              // Бронируемая вещь

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;            // Пользователь, бронирующий вещь

    @Enumerated(EnumType.STRING)
    private BookingStatus status;   // Статус бронирования
}
package ru.practicum.shareit.item.dto;

import lombok.Data;

// Класс для организации сведений о бронировании в ответах на запросы с использованием ItemDtoExtended
@Data
public class BookingDto {
    private final Long id;              //  Идентификатор записи о бронировании вещи
    private final Integer bookerId;     //  Идентификатор пользователя, выполнившего бронирование вещи
}

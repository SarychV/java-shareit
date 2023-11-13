package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@NoArgsConstructor // Для работы тестов, в частности при сборке объекта в контроллере, нужен пустой конструктор.
@AllArgsConstructor
// @Builder дал пакетную область видимости класса,
// возникли проблемы с видимостью в пакете ru.practicum.shareit.request
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
}

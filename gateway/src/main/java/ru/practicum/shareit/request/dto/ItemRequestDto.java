package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor // Для работы тестов, в частности при сборке объекта в контроллере, нужен пустой конструктор.
@AllArgsConstructor
// @Builder дал пакетную область видимости класса,
// возникли проблемы с видимостью в пакете ru.practicum.shareit.request
public class ItemRequestDto {
    private Long id;
    @NotNull
    private String description;
    private LocalDateTime created;
}

package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemAnswerDto implements ItemAnswer {
    private Long id;

    private String name;

    private String description;

    private Long requestId;

    private Boolean available;
}

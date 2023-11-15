package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.model.ItemAnswer;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@Setter @Getter @ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ItemRequestWithAnswersDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    List<ItemAnswer> items;


    public ItemRequestWithAnswersDto(ItemRequest itemRequest, List<ItemAnswer> answers) {
        this.id = itemRequest.getId();
        this.description = itemRequest.getDescription();
        this.created = itemRequest.getCreated();
        this.items = answers;
    }
}

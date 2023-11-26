package ru.practicum.shareit.item.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemWithRequestIdDto extends ItemDto {
    private Long requestId;

    public ItemWithRequestIdDto(Long id, String name, String description, Boolean available, Long requestId) {
        super(id, name, description, available);
        this.requestId = requestId;
    }
}

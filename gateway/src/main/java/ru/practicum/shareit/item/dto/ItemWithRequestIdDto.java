package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemWithRequestIdDto {
    @Positive
    private Long requestId;
    private Long id;
    @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
}

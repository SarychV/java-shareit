package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {
    ItemRequest iRequest = new ItemRequest();
    ItemRequestDto iRequestDto = new ItemRequestDto();

    @BeforeEach
    void setup() {
        iRequest.setId(1L);
        iRequest.setDescription("Description");
        iRequest.setRequesterId(1);
        iRequest.setCreated(LocalDateTime.of(2023, 11, 15, 15, 6));

        iRequestDto.setId(1L);
        iRequestDto.setDescription("Description");
        iRequestDto.setCreated(LocalDateTime.of(2023, 11, 15, 15, 6));
    }

    @Test
    void toItemRequest() {
        ItemRequest result = ItemRequestMapper.toItemRequest(iRequestDto, iRequest.getRequesterId());

        assertEquals(iRequest.getDescription(), result.getDescription());
        assertEquals(iRequest.getRequesterId(), result.getRequesterId());
        assertNotNull(result.getCreated(),"Время должно задаваться при преобразовании объекта");
    }

    @Test
    void toItemRequestDto() {
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(iRequest);

        assertEquals(iRequestDto, result);
    }

    @Test
    void testToItemRequestDto() {

        List<ItemRequest> iRequestList = List.of(iRequest, iRequest, iRequest);

        List<ItemRequestDto> iRequestDtoList = List.of(iRequestDto, iRequestDto, iRequestDto);

        List<ItemRequestDto> result = ItemRequestMapper.toItemRequestDto(iRequestList);

        assertEquals(iRequestDtoList, result);
    }
}
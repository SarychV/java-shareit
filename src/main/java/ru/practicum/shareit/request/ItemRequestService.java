package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewRequest(ItemRequestDto itemRequestDto, Integer requesterId);

    List<ItemRequestDto> getItemRequestListWithAnswersByOwner(Integer ownerId);

    List<ItemRequestDto> getAllItemRequests(Integer requesterId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Integer userId, Long requestId);
}

package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewRequest(ItemRequestDto itemRequestDto, Integer requesterId);

    List<ItemRequestWithAnswersDto> getItemRequestListWithAnswersByOwner(Integer ownerId);

    List<ItemRequestWithAnswersDto> getAllItemRequests(Integer requesterId, Integer from, Integer size);

    ItemRequestWithAnswersDto getItemRequestById(Integer userId, Long requestId);
}

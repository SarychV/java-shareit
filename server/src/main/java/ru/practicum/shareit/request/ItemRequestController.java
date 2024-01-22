package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;

import org.jetbrains.annotations.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestService itemRequestService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        itemRequestService = service;
    }

    @PostMapping
    public ItemRequestDto addNewItemRequest(
                @RequestHeader(HEADER_USER_ID) Integer requesterId,
                @RequestBody(required = false) ItemRequestDto itemRequestDto) {
        return itemRequestService.addNewRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getItemRequestListWithAnswersByOwner(
                @RequestHeader(HEADER_USER_ID) Integer ownerId) {
        return itemRequestService.getItemRequestListWithAnswersByOwner(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithAnswersDto> getAllItemRequests(
                @RequestHeader(HEADER_USER_ID) Integer requesterId,
                @RequestParam(defaultValue = "0") Integer from,
                @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getAllItemRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersDto getItemRequestByIdWithAnswers(
                @RequestHeader(HEADER_USER_ID) @NotNull Integer userId,
                @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}

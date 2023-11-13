package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
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
    public ItemRequestDto addNewItemRequest(@RequestHeader(HEADER_USER_ID) @NotNull Integer requesterId,
                                            @RequestBody(required = false) ItemRequestDto itemRequestDto) {
        return itemRequestService.addNewRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestListWithAnswersByOwner(
            @RequestHeader(HEADER_USER_ID) @NotNull Integer ownerId) {
        return itemRequestService.getItemRequestListWithAnswersByOwner(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(HEADER_USER_ID) @NotNull Integer requesterId,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        return itemRequestService.getAllItemRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestByIdWithAnswers(@RequestHeader(HEADER_USER_ID) @NotNull Integer userId,
                                               @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}

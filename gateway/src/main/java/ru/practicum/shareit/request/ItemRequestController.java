package ru.practicum.shareit.request;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private ItemRequestClient itemRequestClient;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestClient service) {
        itemRequestClient = service;
    }

    @PostMapping
    public ResponseEntity<Object> addNewItemRequest(
                @RequestHeader(HEADER_USER_ID) @NotNull @Positive Integer requesterId,
                @RequestBody(required = false) @Valid ItemRequestDto itemRequestDto) {
        log.info("Adding new item request with requesterId={}, itemRequestDto={}",
                requesterId, itemRequestDto);
        return itemRequestClient.addNewRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestListWithAnswersByOwner(
                @RequestHeader(HEADER_USER_ID) @NotNull @Positive Integer ownerId) {
        log.info("Getting item request list with answers by owner where ownerId={}", ownerId);
        return itemRequestClient.getItemRequestListWithAnswersByOwner(ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
                @RequestHeader(HEADER_USER_ID) @NotNull @Positive Integer requesterId,
                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting all item requests by page with requesterId={}, from={}, size={}",
                requesterId, from, size);
        return itemRequestClient.getAllItemRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestByIdWithAnswers(
                @RequestHeader(HEADER_USER_ID) @NotNull @Positive Integer userId,
                @PathVariable @Positive Long requestId) {
        log.info("Getting item requests by id with answer with requestId={}, userId={}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}

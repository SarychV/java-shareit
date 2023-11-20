package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl service;

    ItemRequestDto itemRequestDto = new ItemRequestDto();
    ItemRequest itemRequest = new ItemRequest();

    Item item1;

    User user1;

    @BeforeEach
    void setup() {
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");
        itemRequestDto.setCreated(LocalDateTime.of(2023, 11, 15, 15, 6));

        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequesterId(1);
        itemRequest.setCreated(LocalDateTime.of(2023, 11, 15, 15, 6));

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item");
        item1.setDescription("good item");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequestId(1L);

        user1 = new User();
        user1.setId(2);
        user1.setName("user1");
        user1.setEmail("User1@mail.co");
    }

    @Test
    void addNewRequest_whenAllArgsValid_thenReturnItemRequestDtoWithId() {
        int requesterId = 1;
        Mockito.when(userRepository.findById(requesterId)).thenReturn(Optional.of(new User()));
        Mockito.when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto irDtoOut = service.addNewRequest(itemRequestDto, requesterId);

        assertEquals(itemRequestDto, irDtoOut);
    }

    @Test
    void addNewRequest_whenRequesterNotFound_thenThrowNotFoundException() {
        int requesterId = 0;
        Mockito.when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.addNewRequest(itemRequestDto, requesterId));

        Mockito.verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void addNewRequest_whenDescriptionIsNull_thenThrowValidationException() {
        int requesterId = 1;
        Mockito.when(userRepository.findById(requesterId)).thenReturn(Optional.of(new User()));
        itemRequestDto.setDescription(null);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addNewRequest(itemRequestDto, requesterId));

        Mockito.verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequestListWithAnswersByOwner_whenValidData_thenReturnItemRequestWithAnswersDto() {
        int ownerId = 1;
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User()));
        Mockito.when(itemRequestRepository.findByRequesterId(eq(ownerId), any(Sort.class)))
                .thenReturn(List.of(itemRequest, itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of());

        List<ItemRequestWithAnswersDto> list = service.getItemRequestListWithAnswersByOwner(ownerId);

        assertNotNull(list);
    }

    @Test
    void getItemRequestListWithAnswersByOwner_whenOwnerNotFound_thenThrowException() {
        int ownerId = 0;
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.getItemRequestListWithAnswersByOwner(ownerId));
    }

    @Test
    void getAllItemRequests_whenValidData_thenReturnListOfItemRequestWithAnswers() {
        int requesterId = 1;
        Integer from = 0;
        Integer size = 5;
        Page<ItemRequest> requestsPage = new PageImpl(List.of(itemRequest));
        Mockito.when(userRepository.findById(requesterId)).thenReturn(Optional.of(new User()));
        Mockito.when(itemRequestRepository.findByRequesterIdNot(eq(requesterId), any(Pageable.class)))
                .thenReturn(requestsPage);
        Mockito.when(itemRepository.findByRequestIdIn(anySet()))
                .thenReturn(List.of(item1));

        List<ItemRequestWithAnswersDto> list = service.getAllItemRequests(requesterId, from, size);

        assertNotNull(list);
    }

    @Test
    void getAllItemRequests_whenRequesterNotFound_thenThrowException() {
        int requesterId = 0;
        int from = 0;
        int size = 5;
        Mockito.when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.getAllItemRequests(requesterId, from, size));
    }

    @Test
    void getItemRequestById_whenValidData_thenReturnRequestWithDto() {
        int userId = 1;
        long requestId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of());

        ItemRequestWithAnswersDto requestWithAnswers = service.getItemRequestById(userId, requestId);

        assertNotNull(requestWithAnswers);
    }

    @Test
    void getItemRequestById_whenUserNotFound_thenThrowException() {
        int userId = 0;
        long requestId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.getItemRequestById(userId, requestId));
    }

    @Test
    void getItemRequestById_whenRequestNotFound_thenThrowException() {
        int userId = 1;
        long requestId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.getItemRequestById(userId, requestId));
    }
}
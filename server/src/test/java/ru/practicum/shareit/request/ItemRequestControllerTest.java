package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    ItemRequestService service;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L, "Tent", LocalDateTime.parse("2022-07-03T19:55:01"));

    @Test
    void addNewItemRequest_whenValidData_thenReturnCreatedItemRequest() throws Exception {
        when(service.addNewRequest(any(), anyInt()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    @Test
    void addNewItemRequest_whenServiceHasException_thenErrorMessage() throws Exception {
        when(service.addNewRequest(any(), anyInt()))
                .thenThrow(new NotFoundException("message"));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error", is("message")));
    }

    @Test
    void getItemRequestListWithAnswersByOwner() throws Exception {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequesterId(1);
        itemRequest.setCreated(LocalDateTime.of(2023, 11, 15, 15, 6));

        ItemRequestWithAnswersDto response = new ItemRequestWithAnswersDto(itemRequest, List.of());

        List<ItemRequestWithAnswersDto> responseList = new ArrayList<>();
        responseList.add(response);

        when(service.getItemRequestListWithAnswersByOwner(1))
                .thenReturn(responseList);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", notNullValue()));
    }

    @Test
    void getAllItemRequests() throws Exception {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequesterId(1);
        itemRequest.setCreated(LocalDateTime.of(2023, 11, 15, 15, 6));

        ItemRequestWithAnswersDto response = new ItemRequestWithAnswersDto(itemRequest, List.of());

        List<ItemRequestWithAnswersDto> responseList = new ArrayList<>();
        responseList.add(response);

        when(service.getAllItemRequests(2, 0, 10))
                .thenReturn(responseList);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "2")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", notNullValue()));
    }

    @Test
    void getItemRequestByIdWithAnswers() throws Exception {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequesterId(1);
        itemRequest.setCreated(LocalDateTime.of(2023, 11, 15, 15, 6));

        ItemRequestWithAnswersDto response = new ItemRequestWithAnswersDto(itemRequest, List.of());


        when(service.getItemRequestById(2, 3L))
                .thenReturn(response);

        mvc.perform(get("/requests/{requestId}", "3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "2")

                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()));
    }
}
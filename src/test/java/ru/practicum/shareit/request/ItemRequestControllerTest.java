package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import ru.practicum.shareit.exception.ExceptionController;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void addNewItemRequest_whenServiceReturnsValidData_thenGetNewItemRequest() throws Exception {
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
    void addNewItemRequest_whenServiceReturnsException_thenErrorMessage() throws Exception {
        when(service.addNewRequest(any(), anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                //.andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                //.andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                //.andExpect(jsonPath("$.created", notNullValue()))
        ;
    }

    @Test
    void getItemRequestListWithAnswersByOwner() {
    }

    @Test
    void getAllItemRequests() {
    }

    @Test
    void getItemRequestByIdWithAnswers() {
    }
}
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemServiceImpl service;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    ItemWithRequestIdDto itemWithRequestIdDto = new ItemWithRequestIdDto(
            1L,
            "name",
            "description",
            true,
            2L
    );

    CommentDto commentDto = new CommentDto();

    ItemDto itemDto = new ItemDto(
            1L,
            "item",
            "very good item",
            true
    );

    ItemDtoExtended itemDtoExtended = new ItemDtoExtended(
            1L,
            "item",
            "very good item",
            true
    );

    @BeforeEach
    void init() {
        commentDto.setId(1L);
        commentDto.setText("very good item");
        commentDto.setAuthorName("user1");
        commentDto.setCreated(LocalDateTime.of(2023, 11, 17, 17, 28));
    }

    @Test
    void createItem() throws Exception {
        Mockito.when(service.addItem(any(ItemWithRequestIdDto.class), eq(1)))
                .thenReturn(itemWithRequestIdDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemWithRequestIdDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithRequestIdDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemWithRequestIdDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithRequestIdDto.getDescription())));
    }

    @Test
    void addCommentToItem() throws Exception {
        Mockito.when(service.addComment(anyInt(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void updateItem() throws Exception {
        Mockito.when(service.updateItem(anyLong(), any(ItemDto.class), anyInt()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItem() throws Exception {
        Mockito.when(service.getItem(anyLong(), anyInt()))
                .thenReturn(itemDtoExtended);

        mvc.perform(get("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(itemDtoExtended))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getAllItemsByOwnerId() throws Exception {
        Mockito.when(service.getAllItemsByOwnerId(anyInt()))
                .thenReturn(List.of(itemDtoExtended));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void lookupItems() throws Exception {
        Mockito.when(service.lookupItemsByText(anyString()))
                .thenReturn(List.of(itemDtoExtended));

        mvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "gold")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }
}
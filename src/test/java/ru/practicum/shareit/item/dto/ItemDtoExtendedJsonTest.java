package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoExtendedJsonTest {
    @Autowired
    private JacksonTester<ItemDtoExtended> json;

    @Test
    void testItemDtoExtended() throws Exception {
        ItemDtoExtended itemDtoExtended = new ItemDtoExtended(
                1L,
                "item",
                "very good item",
                true
        );

        BookingDto lastBooking = new BookingDto(1L, 2);
        BookingDto nextBooking = new BookingDto(2L, 3);

        itemDtoExtended.setLastBooking(lastBooking);
        itemDtoExtended.setNextBooking(nextBooking);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("very good item");
        commentDto.setAuthorName("user2");
        commentDto.setCreated(LocalDateTime.of(2023, 11, 16, 20, 15, 30));

        itemDtoExtended.setComments(List.of(commentDto));


        JsonContent<ItemDtoExtended> result = json.write(itemDtoExtended);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDtoExtended.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoExtended.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDtoExtended.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDtoExtended.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemDtoExtended.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemDtoExtended.getLastBooking().getBookerId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemDtoExtended.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemDtoExtended.getNextBooking().getBookerId());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemDtoExtended.getComments().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(itemDtoExtended.getComments().get(0).getText());
    }
}
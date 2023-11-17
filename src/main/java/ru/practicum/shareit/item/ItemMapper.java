package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoExtended toItemDtoExtended(
            Item item, Booking lastBooking, Booking nextBooking) {
        ItemDtoExtended dto = new ItemDtoExtended(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
        dto.setLastBooking(toBookingDto(lastBooking));
        dto.setNextBooking(toBookingDto(nextBooking));
        return dto;
    }

    public static ItemDtoExtended toItemDtoExtended(
            Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemDtoExtended dto = toItemDtoExtended(item, lastBooking, nextBooking);
        dto.setComments(
                comments.stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public static BookingDto toBookingDto(Booking booking) {
        if (booking != null) {
            return new BookingDto(booking.getId(), booking.getBooker().getId());
        }
        return null;
    }

    public static ItemWithRequestIdDto toItemWithRequestIdDto(Item item) {
        return new ItemWithRequestIdDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public static Item toItem(ItemWithRequestIdDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());
        return item;
    }
}

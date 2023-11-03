package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer ownerId) {
        Item item = ItemMapper.toItem(itemDto);

        if (item.getName().isEmpty()) {
            throw new ValidationException("Поле name должно иметь значение.");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("Поле description не должно быть null.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Поле available не должно быть null.");
        }

        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Владелец с id=%d отсутствует в базе.", ownerId)));
        item.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Integer userId) {
        Item modifiedItem = Item.copyOf(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Вещь с id=%d отсутствует в базе.", itemId))));

        User updater = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d отсутствует в базе.", userId)));

        if (!modifiedItem.getOwner().equals(updater)) {
                throw new NotFoundException(
                        String.format("Пользователь с id=%d не владелец вещи с id=%d.",
                                userId, itemId));
        }

        String name = itemDto.getName();
        if (name != null) {
            modifiedItem.setName(name);
        }

        String description = itemDto.getDescription();
        if (description != null) {
            modifiedItem.setDescription(description);
        }

        Boolean available = itemDto.getAvailable();
        if (available != null) {
            modifiedItem.setAvailable(available);
        }

        return ItemMapper.toItemDto(itemRepository.save(modifiedItem));
    }

    @Override
    public ItemDtoExtended getItem(Long itemId, Integer userId) {
        Booking lastBooking = null;
        Booking nextBooking = null;

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Вещь с id=%d отсутствует в базе.", itemId)));

        // Информацию о бронировании может просматривать только владелец вещи.
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findLastBookingForItem(item, LocalDateTime.now());
            nextBooking = bookingRepository.findNextBookingForItem(item, LocalDateTime.now());
        }
        return ItemMapper.toItemDtoExtended(item, lastBooking, nextBooking, commentRepository.findAllByItem(item));
    }

    @Override
    public List getAllItemsByOwnerId(Integer ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d отсутствует в базе.", ownerId)));
        return itemRepository.findAllByOwner(owner).stream()
                .map(item -> {
                    Booking lastBooking = bookingRepository.findLastBookingForItem(item, LocalDateTime.now());
                    Booking nextBooking = bookingRepository.findNextBookingForItem(item, LocalDateTime.now());
                    return ItemMapper.toItemDtoExtended(item, lastBooking, nextBooking,
                            commentRepository.findAllByItem(item));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List lookupItemsByText(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        return itemRepository.searchByNameDescriptionForText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Integer commenterId, Long itemId, CommentDto commentDto) {
        User commenter = userRepository.findById(commenterId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d отсутствует в базе.", commenterId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Вещь с id=%d отсутствует в базе.", itemId)));
        if (!commenterHasUsedItem(commenter, item)) {
            throw new ValidationException(String.format(
                    "Пользователь id=%d не пользовался вещью id=%d.", commenterId, itemId));
        }
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Комментарий не должен быть пустым.");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, commenter);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    protected boolean commenterHasUsedItem(User commenter, Item item) {
        List<Booking> pastBookings = bookingRepository.findAllByBookerAndEndBefore(
                        commenter, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
        return (pastBookings.stream()
                    .filter(booking ->
                        booking.getItem().getId().equals(item.getId())
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                    .count() > 0);
    }
}

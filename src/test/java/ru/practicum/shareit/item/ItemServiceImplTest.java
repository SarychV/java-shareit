package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl service;

    Item itemIn;
    ItemDto itemDtoIn;
    User user1;
    User user2;

    Booking lastBooking;

    Booking nextBooking;

    CommentDto commentDto;

    ItemWithRequestIdDto itemInWithRequestIdDto;

    @BeforeEach
    void initUsersAndItems() {
        user1 = new User();
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("User1@mail.co");

        user2 = new User();
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("User2@mail.co");

        itemIn = new Item();
        itemIn.setId(1L);
        itemIn.setName("item");
        itemIn.setDescription("good item");
        itemIn.setAvailable(true);
        itemIn.setOwner(user1);
        itemIn.setRequestId(1L);

        itemDtoIn = new ItemDto(1L, "item", "good item", true);

        itemInWithRequestIdDto = new ItemWithRequestIdDto(
                1L,
                "item",
                "good item",
                true,
                1L);
    }

    void initBookings() {
        lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.of(2023, 11, 16, 14, 16, 20));
        lastBooking.setEnd(LocalDateTime.of(2023, 11, 16, 15, 26, 30));
        lastBooking.setItem(itemIn);
        lastBooking.setBooker(user2);
        lastBooking.setStatus(BookingStatus.APPROVED);

        nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(LocalDateTime.of(2023, 11, 17, 14, 16, 20));
        nextBooking.setEnd(LocalDateTime.of(2023, 11, 17, 15, 26, 30));
        nextBooking.setItem(itemIn);
        nextBooking.setBooker(user2);
        nextBooking.setStatus(BookingStatus.APPROVED);
    }

    void initCommentDto() {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("very good item");
        commentDto.setAuthorName("user2");
        commentDto.setCreated(LocalDateTime.of(2023, 11, 16, 20, 15, 30));
    }

    @Test
    void addItem_whenValidData_thenReturnNewItem() {
        int ownerId = 1;
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user1));
        Mockito.when(itemRepository.save(any(Item.class)))
                .then(invocation -> invocation.getArgument(0, Item.class));

        ItemWithRequestIdDto result = service.addItem(itemInWithRequestIdDto, ownerId);

        assertEquals(itemInWithRequestIdDto, result);
    }

    @Test
    void addItem_whenWrongName_thenThrowException() {
        int ownerId = 1;
        itemInWithRequestIdDto.setName("");

        assertThrows(ValidationException.class,
                () -> service.addItem(itemInWithRequestIdDto, ownerId));
        Mockito.verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addItem_whenWrongDescription_thenThrowException() {
        int ownerId = 1;
        itemInWithRequestIdDto.setDescription(null);

        assertThrows(ValidationException.class,
                () -> service.addItem(itemInWithRequestIdDto, ownerId));
        Mockito.verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addItem_whenWrongAvailable_thenThrowException() {
        int ownerId = 1;
        itemInWithRequestIdDto.setAvailable(null);

        assertThrows(ValidationException.class,
                () -> service.addItem(itemInWithRequestIdDto, ownerId));
        Mockito.verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addItem_whenOwnerNotFound_thenThrowException() {
        int ownerId = 1;
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.addItem(itemInWithRequestIdDto, ownerId));
        Mockito.verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_whenValidData_thenReturnUpdatedItem() {
        long itemId = 1;
        int userId = 1;
        itemDtoIn.setName("newName");
        itemDtoIn.setDescription("newDescription");
        itemDtoIn.setAvailable(false);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemIn));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        Mockito.when(itemRepository.save(any(Item.class)))
                .then(invocation -> invocation.getArgument(0, Item.class));

        ItemDto result = service.updateItem(itemId, itemDtoIn, userId);

        assertEquals(itemDtoIn, result);
    }

    @Test
    void updateItem_whenWrongItem_thenThrowException() {
        long itemId = 99;
        int userId = 1;
        itemDtoIn.setName("newName");
        itemDtoIn.setDescription("newDescription");
        itemDtoIn.setAvailable(false);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateItem(itemId, itemDtoIn, userId));

        Mockito.verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_whenWrongUser_thenThrowException() {
        long itemId = 1;
        int userId = 3;
        itemDtoIn.setName("newName");
        itemDtoIn.setDescription("newDescription");
        itemDtoIn.setAvailable(false);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemIn));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateItem(itemId, itemDtoIn, userId));

        Mockito.verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_whenUserNotOwner_thenThrowException() {
        long itemId = 1;
        int userId = 2;
        itemDtoIn.setName("newName");
        itemDtoIn.setDescription("newDescription");
        itemDtoIn.setAvailable(false);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemIn));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user2));

        assertThrows(NotFoundException.class, () -> service.updateItem(itemId, itemDtoIn, userId));

        Mockito.verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItem_whenOwnerGetItem_thenResultWithBooking() {
        initBookings();
        long itemId = 1;
        int userId = 1;
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemIn));
        Mockito.when(bookingRepository.findLastBookingForItem(eq(itemIn), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        Mockito.when(bookingRepository.findNextBookingForItem(eq(itemIn), any(LocalDateTime.class)))
                .thenReturn(nextBooking);
        Mockito.when(commentRepository.findAllByItem(any(Item.class))).thenReturn(List.of());

        ItemDtoExtended result = service.getItem(itemId, userId);

        assertEquals(itemIn.getId(), result.getId());
        assertEquals(itemIn.getName(), result.getName());
        assertEquals(itemIn.getDescription(), result.getDescription());
        assertEquals(itemIn.getAvailable(), result.getAvailable());
        assertEquals(lastBooking.getId(), result.getLastBooking().getId());
        assertEquals(lastBooking.getBooker().getId(), result.getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), result.getNextBooking().getId());
        assertEquals(nextBooking.getBooker().getId(), result.getNextBooking().getBookerId());
    }

    @Test
    void getItem_whenNotOwnerGetItem_thenResultWithBooking() {
        initBookings();
        long itemId = 1;
        int userId = 2;
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemIn));
        Mockito.when(commentRepository.findAllByItem(any(Item.class))).thenReturn(List.of());

        ItemDtoExtended result = service.getItem(itemId, userId);

        assertEquals(itemIn.getId(), result.getId());
        assertEquals(itemIn.getName(), result.getName());
        assertEquals(itemIn.getDescription(), result.getDescription());
        assertEquals(itemIn.getAvailable(), result.getAvailable());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getItem_whenItemNotFound_thenThrowException() {
        initBookings();
        long itemId = 1;
        int userId = 2;
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getItem(itemId, userId));
    }

    @Test
    void getAllItemsByOwnerId_whenValidData_thenGetAllItems() {
        initBookings();
        int ownerId = 1;
        List<Item> listOfItems = List.of(itemIn);
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user1));
        Mockito.when(itemRepository.findAllByOwner(any(User.class)))
                .thenReturn(listOfItems);
        Mockito.when(bookingRepository.findLastBookingForItem(eq(itemIn), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        Mockito.when(bookingRepository.findNextBookingForItem(eq(itemIn), any(LocalDateTime.class)))
                .thenReturn(nextBooking);
        Mockito.when(commentRepository.findAllByItem(any(Item.class))).thenReturn(List.of());

        List<ItemDtoExtended> result = service.getAllItemsByOwnerId(ownerId);

        assertEquals(itemIn.getId(), result.get(0).getId());
        assertEquals(itemIn.getName(), result.get(0).getName());
        assertEquals(itemIn.getDescription(), result.get(0).getDescription());
        assertEquals(itemIn.getAvailable(), result.get(0).getAvailable());
        assertEquals(lastBooking.getId(), result.get(0).getLastBooking().getId());
        assertEquals(lastBooking.getBooker().getId(), result.get(0).getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), result.get(0).getNextBooking().getId());
        assertEquals(nextBooking.getBooker().getId(), result.get(0).getNextBooking().getBookerId());
    }

    @Test
    void getAllItemsByOwnerId_whenOwnerNotFound_thenThrowException() {
        initBookings();
        int ownerId = 1;
        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getAllItemsByOwnerId(ownerId));

        Mockito.verify(itemRepository, never()).findAllByOwner(any(User.class));
    }

    @Test
    void lookupItemsByText_whenValidData_thenReturnListOfItemDto() {
        String text = "item";
        List<Item> listItem = List.of(itemIn);
        Mockito.when(itemRepository.searchByNameDescriptionForText(text))
                .thenReturn(listItem);

        List<ItemDto> result = service.lookupItemsByText(text);

        assertEquals(listItem.size(), result.size());
        assertEquals(itemIn.getId(), result.get(0).getId());
        assertEquals(itemIn.getName(), result.get(0).getName());
        assertEquals(itemIn.getDescription(), result.get(0).getDescription());
        assertEquals(itemIn.getAvailable(), result.get(0).getAvailable());
    }

    @Test
    void lookupItemsByText_whenValidData() {
        String text = "";
        List<ItemDto> result = service.lookupItemsByText(text);

        assertEquals(0, result.size());
    }

    @Test
    void addComment_whenValidData_thenReturnNewComment() {
        int commenterId = 2;
        long itemId = 1;
        initBookings();
        initCommentDto();

        Mockito.when(userRepository.findById(commenterId)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemIn));
        Mockito.when(bookingRepository.findAllByBookerAndEndBefore(
                        any(User.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking));
        Mockito.when(commentRepository.save(any(Comment.class)))
                .then(invocation -> {
                    Comment comment = invocation.getArgument(0, Comment.class);
                    comment.setId(commentDto.getId());
                    comment.setCreated(commentDto.getCreated());
                    return comment;
                });

        CommentDto result = service.addComment(commenterId, itemId, commentDto);

        assertEquals(commentDto, result);
    }

    @Test
    void addComment_whenCommenterNotFound_thenThrowException() {
        int commenterId = 2;
        long itemId = 1;
        initBookings();
        initCommentDto();

        Mockito.when(userRepository.findById(commenterId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addComment(commenterId, itemId, commentDto));
        Mockito.verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_whenItemNotFound_thenThrowException() {
        int commenterId = 2;
        long itemId = 1;
        initBookings();
        initCommentDto();

        Mockito.when(userRepository.findById(commenterId)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addComment(commenterId, itemId, commentDto));
        Mockito.verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_whenCommenterHasNotUsedItem_thenThrowException() {
        int commenterId = 2;
        long itemId = 1;
        initBookings();
        initCommentDto();

        Mockito.when(userRepository.findById(commenterId)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemIn));
        Mockito.when(bookingRepository.findAllByBookerAndEndBefore(
                        any(User.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of());

        assertThrows(ValidationException.class, () -> service.addComment(commenterId, itemId, commentDto));
        Mockito.verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_whenCommentIsEmpty_thenThrowException() {
        int commenterId = 2;
        long itemId = 1;
        initBookings();
        initCommentDto();
        commentDto.setText("");

        Mockito.when(userRepository.findById(commenterId)).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemIn));
        Mockito.when(bookingRepository.findAllByBookerAndEndBefore(
                        any(User.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking));

        assertThrows(ValidationException.class, () -> service.addComment(commenterId, itemId, commentDto));
        Mockito.verify(commentRepository, never()).save(any(Comment.class));
    }
}
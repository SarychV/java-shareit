package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingService service;

    BookingDtoIn bookingDtoInItem1;

    Booking bookingItem1;

    User user1;
    User user2;
    Item item1;

    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void init() {
        bookingDtoInItem1 = new BookingDtoIn(
                1L,
                LocalDateTime.from(now).plusHours(2L),
                LocalDateTime.from(now).plusHours(4L));

        user1 = new User();
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("User1@mail.co");

        user2 = new User();
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("User2@mail.co");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item");
        item1.setDescription("good item");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequestId(1L);

        bookingItem1 = new Booking();
        bookingItem1.setId(1L);
        bookingItem1.setStart(LocalDateTime.from(now).plusHours(2L));
        bookingItem1.setEnd(LocalDateTime.from(now).plusHours(4L));
        bookingItem1.setItem(item1);
        bookingItem1.setBooker(user2);
        bookingItem1.setStatus(BookingStatus.WAITING);
    }

    @Test
    void addBooking_whenValidData_thenAddBooking() {
        int bookerId = 2;
        Mockito.when(itemRepository.findById(bookingDtoInItem1.getItemId()))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .then(invocation -> {
                    Booking booking = invocation.getArgument(0, Booking.class);
                    booking.setId(1L);
                    return booking;
                });

        BookingDtoOut result = service.addBooking(bookerId, bookingDtoInItem1);

        assertEquals(1L, result.getId());
        assertEquals(bookingDtoInItem1.getStart(), result.getStart());
        assertEquals(bookingDtoInItem1.getEnd(), result.getEnd());
        assertEquals(bookingDtoInItem1.getItemId(), result.getItem().getId());
        assertEquals(item1.getName(), result.getItem().getName());
        assertEquals(bookerId, result.getBooker().getId());
    }

    @Test
    void addBooking_whenItemNotFound_thenThrowException() {
        int bookerId = 2;
        Mockito.when(itemRepository.findById(bookingDtoInItem1.getItemId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addBooking(bookerId, bookingDtoInItem1));
        Mockito.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_whenItemNotAvailable_thenThrowException() {
        int bookerId = 2;
        item1.setAvailable(false);
        Mockito.when(itemRepository.findById(bookingDtoInItem1.getItemId()))
                .thenReturn(Optional.of(item1));

        assertThrows(ValidationException.class, () -> service.addBooking(bookerId, bookingDtoInItem1));
        Mockito.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_whenBookerIsOwner_thenThrowException() {
        int bookerId = 1;
        Mockito.when(itemRepository.findById(bookingDtoInItem1.getItemId()))
                .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> service.addBooking(bookerId, bookingDtoInItem1));
        Mockito.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_whenBookerNotFound_thenThrowException() {
        int bookerId = 3;
        Mockito.when(itemRepository.findById(bookingDtoInItem1.getItemId()))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addBooking(bookerId, bookingDtoInItem1));
        Mockito.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void ownerApproveBooking_whenValidData_thenApproveBooking() {
        int ownerId = 1;
        long bookingId = 1;
        boolean approved = true;
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingItem1));
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .then(invocation -> {
                    Booking booking = invocation.getArgument(0, Booking.class);
                    booking.setId(1L);
                    return booking;
                });

        BookingDtoOut result = service.ownerApproveBooking(ownerId, bookingId, approved);

        assertEquals(1L, result.getId());
        assertEquals(bookingDtoInItem1.getStart(), result.getStart());
        assertEquals(bookingDtoInItem1.getEnd(), result.getEnd());
        assertEquals(bookingDtoInItem1.getItemId(), result.getItem().getId());
        assertEquals(item1.getName(), result.getItem().getName());
        assertEquals(user2.getId(), result.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void ownerApproveBooking_whenApproveRejecting_thenApproveBooking() {
        int ownerId = 1;
        long bookingId = 1;
        boolean approved = false;

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingItem1));
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .then(invocation -> {
                    Booking booking = invocation.getArgument(0, Booking.class);
                    booking.setId(1L);
                    return booking;
                });

        BookingDtoOut result = service.ownerApproveBooking(ownerId, bookingId, approved);

        assertEquals(1L, result.getId());
        assertEquals(bookingDtoInItem1.getStart(), result.getStart());
        assertEquals(bookingDtoInItem1.getEnd(), result.getEnd());
        assertEquals(bookingDtoInItem1.getItemId(), result.getItem().getId());
        assertEquals(item1.getName(), result.getItem().getName());
        assertEquals(user2.getId(), result.getBooker().getId());
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void ownerApproveBooking_whenBookingNotFound_thenThrowException() {
        int ownerId = 1;
        long bookingId = 1;
        boolean approved = true;
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.ownerApproveBooking(ownerId, bookingId, approved));
        Mockito.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void ownerApproveBooking_whenUserNotOwner_thenThrowException() {
        int ownerId = 2;  // владельцем является userId = 1
        long bookingId = 1;
        boolean approved = true;
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingItem1));

        assertThrows(NotFoundException.class, () -> service.ownerApproveBooking(ownerId, bookingId, approved));
        Mockito.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void ownerApproveBooking_whenBookingHasStatusApproved_thenThrowException() {
        int ownerId = 1;
        long bookingId = 1;
        boolean approved = true;
        bookingItem1.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingItem1));

        assertThrows(ValidationException.class, () -> service.ownerApproveBooking(ownerId, bookingId, approved));
        Mockito.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void ownerApproveBooking_whenBookingHasStatusRejected_thenThrowException() {
        int ownerId = 1;
        long bookingId = 1;
        boolean approved = false;
        bookingItem1.setStatus(BookingStatus.REJECTED);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(bookingItem1));

        assertThrows(ValidationException.class, () -> service.ownerApproveBooking(ownerId, bookingId, approved));
        Mockito.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingByOwnerOrBooker_whenValidData_thenReturnBooking() {
        int userId = 1;
        long bookingId = 1L;
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingItem1));

        BookingDtoOut result = service.getBookingByOwnerOrBooker(userId, bookingId);

        assertEquals(1L, result.getId());
        assertEquals(bookingItem1.getStart(), result.getStart());
        assertEquals(bookingItem1.getEnd(), result.getEnd());
        assertEquals(bookingItem1.getItem().getId(), result.getItem().getId());
        assertEquals(bookingItem1.getItem().getName(), result.getItem().getName());
        assertEquals(bookingItem1.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingItem1.getStatus(), result.getStatus());

        userId = 2;

        result = service.getBookingByOwnerOrBooker(userId, bookingId);

        assertEquals(1L, result.getId());
        assertEquals(bookingItem1.getStart(), result.getStart());
        assertEquals(bookingItem1.getEnd(), result.getEnd());
        assertEquals(bookingItem1.getItem().getId(), result.getItem().getId());
        assertEquals(bookingItem1.getItem().getName(), result.getItem().getName());
        assertEquals(bookingItem1.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingItem1.getStatus(), result.getStatus());
    }

    @Test
    void getBookingByOwnerOrBooker_whenAlienUser_thenReturnException() {
        int userId = 3;
        long bookingId = 1L;
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingItem1));

        assertThrows(NotFoundException.class, () -> service.getBookingByOwnerOrBooker(userId, bookingId));
    }

    @Test
    void getBookingByOwnerOrBooker_whenUserNotFound_thenReturnException() {
        int userId = 2;
        long bookingId = 1L;
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getBookingByOwnerOrBooker(userId, bookingId));
    }

    @Test
    void getAllByBooker_whenStateAll_thenReturnRelevantPageOfBookings() {
        int bookerId = 2;
        int from = 0;
        int size = 10;
        String stateParam = "ALL";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findAllByBooker(eq(user2), any(Pageable.class)))
                        .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByBooker(bookerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateWaiting_thenReturnRelevantPageOfBookings() {
        int bookerId = 2;
        int from = 0;
        int size = 10;
        String stateParam = "WAITING";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findAllByBookerAndStatus(
                eq(user2), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByBooker(bookerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateRejected_thenReturnRelevantPageOfBookings() {
        int bookerId = 2;
        int from = 0;
        int size = 10;
        String stateParam = "REJECTED";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findAllByBookerAndStatus(
                        eq(user2), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByBooker(bookerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateFuture_thenReturnRelevantPageOfBookings() {
        int bookerId = 2;
        int from = 0;
        int size = 10;
        String stateParam = "FUTURE";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findAllByBookerAndStartAfter(
                eq(user2), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByBooker(bookerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStatePast_thenReturnRelevantPageOfBookings() {
        int bookerId = 2;
        int from = 0;
        int size = 10;
        String stateParam = "PAST";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findAllByBookerAndEndBefore(
                        eq(user2), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByBooker(bookerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByBooker_whenStateCurrent_thenReturnRelevantPageOfBookings() {
        int bookerId = 2;
        int from = 0;
        int size = 10;
        String stateParam = "CURRENT";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(
                        eq(user2), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByBooker(bookerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByBooker_whenUserNotFound_thenThrowException() {
        int bookerId = 2;
        int from = 0;
        int size = 10;
        String stateParam = "CURRENT";
        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getPageByBooker(bookerId, from, size, stateParam));
    }

    @Test
    void getAllByOwner() {
    }

    @Test
    void getAllByOwner_whenStateAll_thenReturnRelevantPageOfBookings() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        String stateParam = "ALL";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwner(eq(user1), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByOwner(ownerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByOwner_whenStateWaiting_thenReturnRelevantPageOfBookings() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        String stateParam = "WAITING";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStatus(
                        eq(user1), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByOwner(ownerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByOwner_whenStateRejected_thenReturnRelevantPageOfBookings() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        String stateParam = "REJECTED";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStatus(
                        eq(user1), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByOwner(ownerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByOwner_whenStateFuture_thenReturnRelevantPageOfBookings() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        String stateParam = "FUTURE";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStartAfter(
                        eq(user1), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByOwner(ownerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByOwner_whenStatePast_thenReturnRelevantPageOfBookings() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        String stateParam = "PAST";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndEndBefore(
                        eq(user1), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByOwner(ownerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByOwner_whenStateCurrent_thenReturnRelevantPageOfBookings() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        String stateParam = "CURRENT";
        @SuppressWarnings({"rawtypes", "unchecked"})
        Page<Booking> bookingPage = new PageImpl(List.of(bookingItem1));

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(
                        eq(user1), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> result = service.getPageByOwner(ownerId, from, size, stateParam);

        assertEquals(bookingItem1.getId(), result.get(0).getId());
    }

    @Test
    void getAllByOwner_whenUserNotFound_thenThrowException() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        String stateParam = "CURRENT";

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getPageByOwner(ownerId, from, size, stateParam));
    }

    @Test
    void getStateStatus_whenKnownStates_thenReturnStateStatusValue() {
        String[] states = {"ALL", "CURRENT", "PAST", "FUTURE", "REJECTED", "WAITING"};
        // Проверить, что все значения states есть в StateStatus.
        for (String state : states) {
            try {
                Enum.valueOf(StateStatus.class, state);
            } catch (IllegalArgumentException e) {
                assertNull(e, "Одно из значений массива states не принадлежит StateStatus.");
            }
        }

        // Проверить, что выполняется правильное преобразование state в StateStatus.
        for (String state : states) {
            StateStatus result = service.getStateStatus(state);
            assertEquals(state, result.name());
        }
    }

    @Test
    void getStateStatus_whenStateIsNull_thenReturnStateStatusAll() {
        assertEquals(StateStatus.ALL, service.getStateStatus(null));
    }

    @Test
    void getStateStatus_whenStateIsUnknown_thenThrowException() {
        assertThrows(ValidationException.class, () -> service.getStateStatus("UNKNOWN"));
    }

    @Test
    void mapStateToBookingStatuses() {
        assertEquals(BookingStatus.WAITING, service.mapStateToBookingStatuses(StateStatus.WAITING));
        assertEquals(BookingStatus.REJECTED, service.mapStateToBookingStatuses(StateStatus.REJECTED));
    }
}
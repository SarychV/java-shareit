package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class BookingServiceImpl {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDtoOut addBooking(Integer bookerId, BookingDtoIn bookingDtoIn) {
        bookingDtoIn.hasValidTimesOrThrow();

        Long itemId = bookingDtoIn.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с id=%d отсутствует в базе.", itemId)));
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Вещь с id=%d занята.", itemId));
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d владелец вещи с id=%d", bookerId, itemId));
        }

        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d отсутствует в базе.", bookerId)));

        Booking booking = new Booking();
        booking.setStart(bookingDtoIn.getStart());
        booking.setEnd(bookingDtoIn.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    public BookingDtoOut ownerApproveBooking(Integer ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Бронирование с id=%d отсутствует в базе.", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не владелец вещи.", ownerId));
        }
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new ValidationException(
                        String.format("Бронирование id=%d уже имеет статус 'APPROVED'.", bookingId));
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            if (booking.getStatus().equals(BookingStatus.REJECTED)) {
                throw new ValidationException(
                        String.format("Бронирование id=%d уже имеет статус 'REJECTED'.", bookingId));
            }
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    public BookingDtoOut getBookingByOwnerOrBooker(Integer userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Бронирование с id=%d отсутствует в базе.", bookingId)));
        if (!booking.getBooker().getId().equals(userId)                 // пользователь не арендатор
            && !booking.getItem().getOwner().getId().equals(userId)) {  // пользователь не владелец вещи
            throw new NotFoundException("Доступ пользователя к информации о бронировании отклонен.");
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    public List getAllByBooker(Integer bookerId, String stateParam) {
        List<Booking> bookings;
        StateStatus state = getStateStatus(stateParam);
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d отсутствует в базе.", bookerId)));
        switch (state) {
            case WAITING:
            case REJECTED:
                BookingStatus bs = mapStateToBookingStatuses(state);
                bookings = bookingRepository.findAllByBookerAndStatus(booker, bs,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfter(booker, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(booker, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(booker,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                bookings = bookingRepository.findAllByBooker(booker, Sort.by(Sort.Direction.DESC, "start"));
        }
        return BookingMapper.toListBookingDtoOut(bookings);
    }

    public List getAllByOwner(Integer ownerId, String stateParam) {
        StateStatus state = getStateStatus(stateParam);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d отсутствует в базе.", ownerId)));
        switch (state) {
            case WAITING:
            case REJECTED:
                BookingStatus bs = mapStateToBookingStatuses(state);
                return BookingMapper.toListBookingDtoOut(
                        bookingRepository.findAllByItemOwnerAndStatus(
                                owner, bs, Sort.by(Sort.Direction.DESC, "start")));
            case FUTURE:
                return BookingMapper.toListBookingDtoOut(
                        bookingRepository.findAllByItemOwnerAndStartAfter(
                                owner, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            case PAST:
                return BookingMapper.toListBookingDtoOut(
                        bookingRepository.findAllByItemOwnerAndEndBefore(
                                owner, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            case CURRENT:
                return BookingMapper.toListBookingDtoOut(
                        bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(owner,
                                LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            default:
                return BookingMapper.toListBookingDtoOut(
                        bookingRepository.findAllByItemOwner(owner, Sort.by(Sort.Direction.DESC, "start")));
        }
    }

    protected StateStatus getStateStatus(final String state) {
        StateStatus result;

        if (state == null) return StateStatus.ALL;
        switch (state) {
            case "ALL":
                return StateStatus.ALL;
            case "CURRENT":
                return StateStatus.CURRENT;
            case "PAST":
                return StateStatus.PAST;
            case "FUTURE":
                return StateStatus.FUTURE;
            case "REJECTED":
                return StateStatus.REJECTED;
            case "WAITING":
                return StateStatus.WAITING;
            default:
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    // Преобразование StateStatus.REJECTED и StateStatus.WAITING
    // в BookingStatus.REJECTED и BookingStatus.WAITING соответственно.
    protected BookingStatus mapStateToBookingStatuses(StateStatus state) {
        return Enum.valueOf(BookingStatus.class, state.name());
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.validation.PageParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
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

    public List<BookingDtoOut> getAllByBooker(Integer bookerId, Integer from, Integer size, String stateParam) {
        Page<Booking> bookings;
        StateStatus state = getStateStatus(stateParam);
        PageParams.validate(from, size);
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d отсутствует в базе.", bookerId)));

        Sort sortByStart = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(from / size, size, sortByStart);

        switch (state) {
            case WAITING:
            case REJECTED:
                BookingStatus bs = mapStateToBookingStatuses(state);
                bookings = bookingRepository.findAllByBookerAndStatus(booker, bs, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfter(booker, LocalDateTime.now(), page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(booker, LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(booker,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            default:
                bookings = bookingRepository.findAllByBooker(booker, page);
        }
        return BookingMapper.toListBookingDtoOut(bookings.stream().collect(Collectors.toList()));
    }

    public List<BookingDtoOut> getAllByOwner(Integer ownerId, Integer from, Integer size, String stateParam) {
        Page<Booking> bookings;
        StateStatus state = getStateStatus(stateParam);

        PageParams.validate(from,size);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id=%d отсутствует в базе.", ownerId)));

        Sort sortByStart = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(from, size, sortByStart);

        switch (state) {
            case WAITING:
            case REJECTED:
                BookingStatus bs = mapStateToBookingStatuses(state);
                bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, bs, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(owner, LocalDateTime.now(), page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(owner, LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(
                        owner, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            default:
                bookings = bookingRepository.findAllByItemOwner(owner, page);
        }
        return BookingMapper.toListBookingDtoOut(bookings.stream().collect(Collectors.toList()));
    }

    protected StateStatus getStateStatus(final String state) {
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
                // Это исключение необходимо для прохождения теста.
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    // Преобразование StateStatus.REJECTED и StateStatus.WAITING
    // в BookingStatus.REJECTED и BookingStatus.WAITING соответственно.
    protected BookingStatus mapStateToBookingStatuses(StateStatus state) {
        return Enum.valueOf(BookingStatus.class, state.name());
    }
}

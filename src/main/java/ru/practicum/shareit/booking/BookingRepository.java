package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Sort sort);

    // StateStatus.FUTURE
    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime startTime, Sort sort);

    // StateStatus.CURRENT
    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(
            User booker, LocalDateTime startTime, LocalDateTime endTime, Sort sort);

    // StateStatus.PAST
    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime endTime, Sort sort);

    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(User booker, BookingStatus status, Sort sort);

    // StateStatus.FUTURE
    List<Booking> findAllByItemOwnerAndStartAfter(User booker, LocalDateTime startTime, Sort sort);

    // StateStatus.CURRENT
    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(
            User booker, LocalDateTime startTime, LocalDateTime endTime, Sort sort);

    // StateStatus.PAST
    List<Booking> findAllByItemOwnerAndEndBefore(User booker, LocalDateTime endTime, Sort sort);

    @Query("select bo from Booking bo " +
            "where bo.start=" +
            "(select max(b.start) from Booking b where b.item=?1 and b.start < ?2 and b.status='APPROVED') " +
            "and bo.item = ?1")
    Booking findLastBookingForItem(Item item, LocalDateTime time);

    @Query("select bo from Booking bo " +
            "where bo.start=" +
            "(select min(b.start) from Booking b where b.item=?1 and b.start > ?2 and b.status='APPROVED') " +
            "and bo.item = ?1")
    Booking findNextBookingForItem(Item item, LocalDateTime time);

    Page<Booking> findAllByBooker(User booker, Pageable page);

    Page<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Pageable page);

    // StateStatus.FUTURE
    Page<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime startTime, Pageable page);

    // StateStatus.CURRENT
    Page<Booking> findAllByBookerAndStartBeforeAndEndAfter(
            User booker, LocalDateTime startTime, LocalDateTime endTime, Pageable page);

    // StateStatus.PAST
    Page<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime endTime, Pageable page);

    Page<Booking> findAllByItemOwner(User owner, Pageable page);

    Page<Booking> findAllByItemOwnerAndStatus(User booker, BookingStatus status, Pageable page);

    // StateStatus.FUTURE
    Page<Booking> findAllByItemOwnerAndStartAfter(User booker, LocalDateTime startTime, Pageable page);

    // StateStatus.CURRENT
    Page<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(
            User booker, LocalDateTime startTime, LocalDateTime endTime, Pageable page);

    // StateStatus.PAST
    Page<Booking> findAllByItemOwnerAndEndBefore(User booker, LocalDateTime endTime, Pageable page);


}

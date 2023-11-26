package ru.practicum.shareit.booking.model;

public enum StateStatus {
    ALL,            // дополнительные ограничения не определены
    CURRENT,        // текущее время между start_date и end_date
    PAST,           // текущее время после end_date
    FUTURE,         // текущее время до start_time
    REJECTED,       // status = REJECTED
    WAITING         // status = WAITING
}

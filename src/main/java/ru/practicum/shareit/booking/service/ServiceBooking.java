package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.util.List;

public interface ServiceBooking {
    BookingDto createBooking(CreateBooking createBookingDto, Long userId);
    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);
    BookingDto getBookingById(Long bookingId, Long userId);
    List<BookingDto> getBookings(Long userId, StatusBooking status);
    List<BookingDto> getBookingsByOwnerId(Long ownerId, StatusBooking status);
}
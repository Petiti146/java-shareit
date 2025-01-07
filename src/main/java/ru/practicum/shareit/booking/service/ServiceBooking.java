package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.util.List;

public interface ServiceBooking {

    BookingDto createBooking(CreateBooking booking, Long userId);

    BookingDto acceptBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookings(Long userId, StatusBooking status);

    List<BookingDto> getBookingsByOwnerId(Long ownerId, StatusBooking status);
}
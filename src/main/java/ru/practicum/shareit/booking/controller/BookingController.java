package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.ServiceBooking;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final ServiceBooking bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CreateBooking booking) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.acceptBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false) StatusBooking status) {
        return bookingService.getBookings(userId, status);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(required = false) StatusBooking status) {
        return bookingService.getBookingsByOwnerId(ownerId, status);
    }
}
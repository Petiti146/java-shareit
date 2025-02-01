package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.RepositoryBooking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnAvaliableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceBookingImpl implements ServiceBooking {

    private final RepositoryBooking bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto createBooking(CreateBooking bookingCreate, Long userId) {
        log.info("Инициализация нового бронирования: {}, инициированного пользователем с id : {}", bookingCreate, userId);

        validateBookingDates(bookingCreate);

        User booker = getUserById(userId);
        Item item = getItemById(bookingCreate.getItemId());

        validateItemAvailable(item);

        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingCreate, item, booker));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        log.info("Обработка подтверждения бронирования с ID: {}", bookingId);
        Booking booking = findById(bookingId);
        validateOwnerAccess(ownerId, booking);

        booking.setStatus(approved ? StatusBooking.APPROVED : StatusBooking.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("Запрос информации о бронировании с ID: {}", bookingId);
        Booking booking = findById(bookingId);
        validateUserAccess(userId, booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookings(Long userId, StatusBooking status) {
        log.info("Запрос информации о бронированиях для пользователя с ID: {}", userId);

        getUserById(userId);

        return status == null ?
                BookingMapper.mapToBookingDto(bookingRepository.findAllByBookerId(userId)) :
                BookingMapper.mapToBookingDto(bookingRepository.findAllByBookerIdAndStatus(userId, status));
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long ownerId, StatusBooking status) {
        log.info("Запрос информации о бронированиях для предметов владельца с ID: {}", ownerId);
        getUserById(ownerId);

        return status == null ?
                BookingMapper.mapToBookingDto(bookingRepository.findAllByItemOwnerId(ownerId)) :
                BookingMapper.mapToBookingDto(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, status));
    }

    private Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование с id: " + id + " не найдено"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден"));
    }

    private void validateBookingDates(CreateBooking bookingCreate) {
        if (bookingCreate.getStart().equals(bookingCreate.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не может совпадать с датой окончания");
        }
        if (bookingCreate.getStart().isAfter(bookingCreate.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна быть позже даты окончания");
        }
    }

    private void validateItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new UnAvaliableException("Предмет с id: " + item.getId() + " недоступен для бронирования");
        }
    }

    private void validateOwnerAccess(Long ownerId, Booking booking) {
        if (!ownerId.equals(booking.getItem().getOwnerId())) {
            throw new ForbiddenException("Только владелец предмета может изменять статус бронирования");
        }
    }

    private void validateUserAccess(Long userId, Booking booking) {
        if (!(userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwnerId()))) {
            throw new ForbiddenException("Просмотр информации о бронировании разрешен только владельцу предмета или пользователю, который его забронировал");
        }
    }
}
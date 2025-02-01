package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.RepositoryBooking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.DtoComment;
import ru.practicum.shareit.item.dto.DtoItem;
import ru.practicum.shareit.item.dto.DtoItemBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.ServiceUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ServiceUser userService;
    private final RepositoryBooking bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public DtoItem createItem(DtoItem item) {
        userService.getUserById(item.getOwnerId());
        log.info("Создание нового предмета: {}", item);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(item)));
    }

    @Override
    public List<DtoItemBooking> getAllItemByOwnerId(Long ownerId) {
        log.info("Получение всех предметов по ID владельца: {}", ownerId);
        userService.getUserById(ownerId);

        return mapToItemDtoBooking(itemRepository.findByOwnerId(ownerId));
    }

    @Override
    public DtoItemBooking getItemById(Long itemId) {
        log.info("Получение предмета по ID: {}", itemId);
        Item item = findById(itemId);
        return mapToItemDtoBooking(item);
    }

    @Override
    @Transactional
    public DtoItem updateItem(DtoItem item) {
        log.info("Обновление предмета по ID: {}", item.getId());
        userService.getUserById(item.getOwnerId());
        Item haveItem = findById(item.getId());
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItemOnUpdate(item, haveItem)));
    }

    @Override
    @Transactional
    public void deleteItemByItemId(Long itemId) {
        log.info("Удаление предмета по ID: {}", itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<DtoItem> searchItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        log.info("Поиск предметов по тексту: {}", text);
        return itemRepository.findByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public DtoComment createComment(DtoComment comment, Long userId, Long itemId) {
        if (bookingRepository.checkHaveBooking(itemId, userId) > 0) {
            log.info("Добавление комментария к предмету: {}", itemId);
            Item item = findById(itemId);
            User user = UserMapper.toUser(userService.getUserById(userId));
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(comment, item, user)));
        }
        throw new BadRequestException("Чтобы оставить комментарий, пользователь должен взять предмет и вернуть его.");
    }

    private Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден"));
    }

    private DtoItemBooking mapToItemDtoBooking(Item item) {
        Booking lastBooking = bookingRepository.findLastBooking(item.getId());
        Booking nextBooking = bookingRepository.findNextBooking(item.getId());
        List<DtoComment> comments = CommentMapper.mapToCommentDto(commentRepository.findAllByItemId(item.getId()));
        return ItemMapper.toItemDtoBooking(item, lastBooking, nextBooking, comments);
    }

    private List<DtoItemBooking> mapToItemDtoBooking(List<Item> items) {
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        Map<Long, Booking> lastBookings = bookingRepository.findLastBookings(itemIds).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        Map<Long, Booking> nextBookings = bookingRepository.findNextBookings(itemIds).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        Map<Long, List<DtoComment>> comments = commentRepository.findAllByItemIds(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())));

        return items.stream()
                .map(item -> ItemMapper.toItemDtoBooking(item, lastBookings.get(item.getId()),
                        nextBookings.get(item.getId()), comments.get(item.getId())))
                .toList();
    }
}
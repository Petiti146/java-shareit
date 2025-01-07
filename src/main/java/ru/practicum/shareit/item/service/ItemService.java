
package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.DtoComment;
import ru.practicum.shareit.item.dto.DtoItem;
import ru.practicum.shareit.item.dto.DtoItemBooking;

import java.util.List;

public interface ItemService {

    DtoItem createItem(DtoItem item);

    List<DtoItemBooking> getAllItemByOwnerId(Long ownerId);

    DtoItemBooking getItemById(Long itemId);

    DtoItem updateItem(DtoItem item);

    void deleteItemByItemId(Long itemId);

    List<DtoItem> searchItemsByText(String text);

    DtoComment createComment(DtoComment comment, Long userId, Long itemId);
}

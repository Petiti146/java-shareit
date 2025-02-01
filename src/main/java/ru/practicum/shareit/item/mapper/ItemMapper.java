
package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.DtoComment;
import ru.practicum.shareit.item.dto.DtoItem;
import ru.practicum.shareit.item.dto.DtoItemBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public class ItemMapper {
    public static DtoItem toItemDto(Item item) {
        DtoItem itemDto = new DtoItem();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(item.getOwnerId());
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public static Item toItemOnUpdate(DtoItem itemDto, Item item) {
        Item newItem = new Item();
        newItem.setId(itemDto.getId());
        newItem.setName(Optional.ofNullable(itemDto.getName()).orElse(item.getName()));
        newItem.setDescription(Optional.ofNullable(itemDto.getDescription()).orElse(item.getDescription()));
        newItem.setAvailable(Optional.ofNullable(itemDto.getAvailable()).orElse(item.getAvailable()));
        newItem.setOwnerId(item.getOwnerId());
        newItem.setRequestId(item.getRequestId());
        return newItem;
    }

    public static Item toItem(DtoItem itemDto) {
        Item newItem = new Item();
        newItem.setId(itemDto.getId());
        newItem.setName(itemDto.getName());
        newItem.setDescription(itemDto.getDescription());
        newItem.setAvailable(itemDto.getAvailable());
        newItem.setOwnerId(itemDto.getOwnerId());
        newItem.setRequestId(itemDto.getRequestId());
        return newItem;
    }

    public static DtoItemBooking toItemDtoBooking(Item item, Booking lastBooking, Booking nextBooking,
                                                  List<DtoComment> comments) {
        DtoItemBooking dto = new DtoItemBooking();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        dto.setRequestId(item.getRequestId());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        return dto;
    }
}

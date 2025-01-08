
package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.DtoComment;
import ru.practicum.shareit.item.dto.DtoItem;
import ru.practicum.shareit.item.dto.DtoItemBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public DtoItem createItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId, @Valid @RequestBody DtoItem item) {
        item.setOwnerId(ownerId);
        return itemService.createItem(item);
    }

    @PatchMapping("/{itemId}")
    public DtoItem updateItem(@PathVariable Long itemId, @RequestBody DtoItem item,
                              @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        item.setId(itemId);
        item.setOwnerId(ownerId);

        return itemService.updateItem(item);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<DtoItemBooking> getAllItemByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllItemByOwnerId(ownerId);
    }

    @GetMapping("/{itemId}")
    @Transactional(readOnly = true)
    public DtoItemBooking getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<DtoItem> searchItemsByText(@RequestParam String text) {
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public DtoComment createComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                    @RequestBody DtoComment comment) {
        return itemService.createComment(comment, userId, itemId);
    }
}

package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.DtoComment;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static Comment toComment(DtoComment commentDto, Item item, User user) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static DtoComment toCommentDto(Comment comment) {
        DtoComment commentDto = new DtoComment();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItemId(comment.getItem().getId());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static List<DtoComment> mapToCommentDto(Iterable<Comment> comments) {
        List<DtoComment> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(toCommentDto(comment));
        }
        return dtos;
    }
}
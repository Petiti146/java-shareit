
package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST) // 409
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
        log.warn(message);
    }
}

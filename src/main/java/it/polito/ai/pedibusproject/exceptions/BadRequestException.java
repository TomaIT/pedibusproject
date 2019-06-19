package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {
    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST,"Bad Request");
    }

    public BadRequestException(String reason) {
        super(HttpStatus.BAD_REQUEST,"Bad Request: "+ reason);
    }

    public BadRequestException(String reason, Throwable cause) {
        super(HttpStatus.BAD_REQUEST,"Bad Request: "+ reason, cause);
    }
}

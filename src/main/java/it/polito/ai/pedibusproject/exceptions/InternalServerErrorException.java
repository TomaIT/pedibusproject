package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InternalServerErrorException extends ResponseStatusException {
    public InternalServerErrorException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error");
    }

    public InternalServerErrorException(String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error: "+ reason);
    }

    public InternalServerErrorException(String reason, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error: "+ reason, cause);
    }
}

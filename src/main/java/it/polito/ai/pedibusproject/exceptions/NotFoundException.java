package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotFoundException extends ResponseStatusException {
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND,"Not Found");
    }

    public NotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND,"Not Found: "+ reason);
    }

    public NotFoundException(String reason,Throwable cause) {
        super(HttpStatus.NOT_FOUND,"Not Found: "+ reason, cause);
    }
}

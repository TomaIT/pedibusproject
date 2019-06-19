package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ForbiddenException extends ResponseStatusException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN,"Forbidden");
    }

    public ForbiddenException(String reason) {
        super(HttpStatus.FORBIDDEN,"Forbidden: "+ reason);
    }

    public ForbiddenException(String reason, Throwable cause) {
        super(HttpStatus.FORBIDDEN,"Forbidden: "+ reason, cause);
    }
}

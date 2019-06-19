package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExpiredException extends ResponseStatusException {
    public ExpiredException() {
        super(HttpStatus.NOT_FOUND,"Expired");
    }

    public ExpiredException(String reason) {
        super(HttpStatus.NOT_FOUND,"Expired: "+ reason);
    }

    public ExpiredException(String reason, Throwable cause) {
        super(HttpStatus.NOT_FOUND,"Expired: "+ reason, cause);
    }
}

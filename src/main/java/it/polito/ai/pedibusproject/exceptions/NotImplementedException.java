package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotImplementedException extends ResponseStatusException {
    public NotImplementedException() {
        super(HttpStatus.NOT_IMPLEMENTED);
    }

    public NotImplementedException(String reason) {
        super(HttpStatus.NOT_IMPLEMENTED,"Not Implemented: "+ reason);
    }

    public NotImplementedException(String reason, Throwable cause) {
        super(HttpStatus.NOT_IMPLEMENTED,"Not Implemented: "+ reason, cause);
    }
}

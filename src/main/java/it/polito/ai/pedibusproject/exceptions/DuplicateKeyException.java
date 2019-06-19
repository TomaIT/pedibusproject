package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateKeyException extends ResponseStatusException {
    public DuplicateKeyException() {
        super(HttpStatus.BAD_REQUEST,"Duplicate Key");
    }

    public DuplicateKeyException(String reason) {
        super(HttpStatus.BAD_REQUEST,"Duplicate Key: "+ reason);
    }

    public DuplicateKeyException(String reason,Throwable cause) {
        super(HttpStatus.BAD_REQUEST,"Duplicate Key: "+ reason, cause);
    }
}

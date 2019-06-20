package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateKeyException extends ResponseStatusException {
    public DuplicateKeyException() {
        super(HttpStatus.CONFLICT,"Duplicate Key");
    }

    public DuplicateKeyException(String reason) {
        super(HttpStatus.CONFLICT,"Duplicate Key: "+ reason);
    }

    public DuplicateKeyException(String reason,Throwable cause) {
        super(HttpStatus.CONFLICT,"Duplicate Key: "+ reason, cause);
    }
}

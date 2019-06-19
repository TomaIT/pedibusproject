package it.polito.ai.pedibusproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedException extends ResponseStatusException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED,"Unauthorized");
    }

    public UnauthorizedException(String reason) {
        super(HttpStatus.UNAUTHORIZED,"Unauthorized: "+ reason);
    }

    public UnauthorizedException(String reason, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED,"Unauthorized: "+ reason, cause);
    }
}

package com.neekly_report.whirlwind.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoDateTimeFormatException extends RuntimeException {
    public NoDateTimeFormatException(String message) {
        super(message);
    }
}

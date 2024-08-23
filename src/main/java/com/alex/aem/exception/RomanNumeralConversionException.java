package com.alex.aem.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RomanNumeralConversionException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(RomanNumeralConversionException.class);
    private final String errorCode;

    /**
     * Constructs a new RomanNumeralConversionException with the specified detail message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public RomanNumeralConversionException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
        log.error("RomanNumeralConversionException created: {} (Error code: {})", message, errorCode);
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}

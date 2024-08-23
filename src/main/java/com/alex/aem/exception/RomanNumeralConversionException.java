package com.alex.aem.exception;

public class RomanNumeralConversionException extends RuntimeException {
    private final String errorCode;

    /**
     * Constructs a new RomanNumeralConversionException with the specified detail message and error code.
     *
     * @param message   the detail message
     * @param errorCode the error code
     */
    public RomanNumeralConversionException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
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

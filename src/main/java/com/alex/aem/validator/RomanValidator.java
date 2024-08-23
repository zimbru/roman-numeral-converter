package com.alex.aem.validator;

import com.alex.aem.exception.RomanNumeralConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validator for input data
 */
@Component
public class RomanValidator {

    private static final Logger log = LoggerFactory.getLogger(RomanValidator.class);

    /**
     * Validates the input range for Roman numeral conversion.
     *
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @throws RomanNumeralConversionException if the input is invalid.
     */
    public void validateInput(final int min, final int max) {
        if (min > max) {
            log.error("Invalid range: min ({}) is greater than max ({})", min, max);
            throw new RomanNumeralConversionException("Min must be less than or equal to max", "INVALID_RANGE");
        }
        if (min < 1 || max > 3999) {
            log.error("Out of range: min={}, max={}", min, max);
            throw new RomanNumeralConversionException("Numbers must be between 1 and 3999", "OUT_OF_RANGE");
        }
    }
}

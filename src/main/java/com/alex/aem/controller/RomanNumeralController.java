package com.alex.aem.controller;


import com.alex.aem.data.ErrorResponse;
import com.alex.aem.data.RomanNumeralConversionsData;
import com.alex.aem.data.RomanNumeralData;
import com.alex.aem.exception.RomanNumeralConversionException;
import com.alex.aem.service.NumberConverterService;
import com.alex.aem.service.RomanNumeralConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling Roman numeral conversion requests.
 */
@RestController
@RequestMapping("/romannumeral")
public class RomanNumeralController {

    private static final Logger log = LoggerFactory.getLogger(RomanNumeralController.class);

    private final NumberConverterService converterService;

    public RomanNumeralController(final RomanNumeralConverterService romanNumeralConverterService) {
        this.converterService = romanNumeralConverterService;
    }

    /**
     * Handles all Roman numeral conversion requests.
     * This method can handle single number conversion and range conversion.
     *
     * @param query The single number to convert (optional).
     * @param min   The lower bound of the range (optional).
     * @param max   The upper bound of the range (optional).
     * @return ResponseEntity with the converted Roman numeral(s) or error response.
     */
    @GetMapping
    public ResponseEntity<?> handleConversion(
            @RequestParam(required = false) final Integer query,
            @RequestParam(required = false) final Integer min,
            @RequestParam(required = false) final Integer max) {

        final Map<String, Integer> params = new HashMap<>();
        if (query != null) {
            params.put("query", query);
        }
        if (min != null) {
            params.put("min", min);
        }
        if (max != null) {
            params.put("max", max);
        }

        log.info("Received conversion request with parameters: {}", params);

        try {
            if (query != null) {
                return handleSingleConversion(query);
            } else if (min != null && max != null) {
                return handleRangeConversion(min, max);
            } else {
                log.warn("Invalid parameter combination: {}", params);
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("INVALID_PARAMETERS", "Either 'query' or both 'min' and 'max' must be provided"));
            }
        } catch (final RomanNumeralConversionException e) {
            log.warn("Conversion error for parameters {}: {}", params, e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }


    private ResponseEntity<?> handleSingleConversion(final int query) {
        log.info("Converting single number: {}", query);
        final RomanNumeralData result = converterService.convertOneNumber(query);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<?> handleRangeConversion(final int min, final int max) {
        log.info("Converting range from {} to {}", min, max);
        final RomanNumeralConversionsData result = converterService.convertMultipleNumbers(min, max);
        return ResponseEntity.ok(result);
    }

    /**
     * Handles missing request parameter exceptions.
     *
     * @param ex The exception thrown when a required parameter is missing.
     * @return ResponseEntity with an error response.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(final MissingServletRequestParameterException ex) {
        final String paramName = ex.getParameterName();
        final String message = paramName + " parameter is missing";
        log.warn("Missing parameter: {}", paramName);
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("MISSING_PARAMETER", message));
    }

    /**
     * Handles general exceptions.
     *
     * @param ex The exception thrown.
     * @return ResponseEntity with an error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(final Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }
}

package com.alex.aem.controller;

import com.alex.aem.data.RomanNumeralConversionsData;
import com.alex.aem.data.RomanNumeralData;
import com.alex.aem.exception.RomanNumeralConversionException;
import com.alex.aem.service.RomanNumeralConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RomanNumeralControllerTest {

    @Mock
    private RomanNumeralConverterService converterService;

    private RomanNumeralController controller;

    @BeforeEach
    void setUp() {
        controller = new RomanNumeralController(converterService);
    }

    @Test
    void Single_number_conversion_returns_correct_result() {
        when(converterService.convertOneNumber(5)).thenReturn(new RomanNumeralData(5, "V"));

        final ResponseEntity<?> response = controller.handleConversion(5, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(RomanNumeralData.class, response.getBody());
        final RomanNumeralData result = (RomanNumeralData) response.getBody();
        assertEquals(5, result.input());
        assertEquals("V", result.output());
    }

    @Test
    void Range_conversion_returns_correct_result() {
        final List<RomanNumeralData> conversions = List.of(
                new RomanNumeralData(1, "I"),
                new RomanNumeralData(2, "II"),
                new RomanNumeralData(3, "III")
        );
        when(converterService.convertMultipleNumbers(1, 3))
                .thenReturn(new RomanNumeralConversionsData(conversions));

        final ResponseEntity<?> response = controller.handleConversion(null, 1, 3);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(RomanNumeralConversionsData.class, response.getBody());
        final RomanNumeralConversionsData result = (RomanNumeralConversionsData) response.getBody();
        assertEquals(3, result.conversions().size());

        assertTrue(result.conversions().stream()
                .anyMatch(data -> data.input() == 1 && "I".equals(data.output())));
        assertTrue(result.conversions().stream()
                .anyMatch(data -> data.input() == 3 && "III".equals(data.output())));
    }

    @Test
    void Invalid_missing_max_parameter_combination_returns_bad_request() {
        final ResponseEntity<?> response = controller.handleConversion(null, 1, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Invalid_missing_min_parameter_combination_returns_bad_request() {
        final ResponseEntity<?> response = controller.handleConversion(null, null, 7);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Invalid_missing_all_parameters_combination_returns_bad_request() {
        final ResponseEntity<?> response = controller.handleConversion(null, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void Conversion_exception_returns_bad_request() {
        when(converterService.convertOneNumber(4000))
                .thenThrow(new RomanNumeralConversionException("Number out of range", "OUT_OF_RANGE"));

        final ResponseEntity<?> response = controller.handleConversion(4000, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
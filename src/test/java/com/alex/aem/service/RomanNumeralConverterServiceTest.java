package com.alex.aem.service;

import com.alex.aem.data.RomanNumeralConversionsData;
import com.alex.aem.data.RomanNumeralData;
import com.alex.aem.exception.RomanNumeralConversionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class RomanNumeralConverterServiceTest {

    private RomanNumeralConverterService service;

    @BeforeEach
    void setUp() {
        service = new RomanNumeralConverterService();
        ReflectionTestUtils.setField(service, "configuredBatchSize", 0);
    }

    @Test
    void canaryTest() {
        assertTrue(true);
    }

    @Test
    void Single_small_number_conversion_returns_correct_result() {
        final RomanNumeralData result = service.convertOneNumber(9);
        assertEquals(9, result.input());
        assertEquals("IX", result.output());
    }

    @Test
    void Single_big_number_conversion_returns_correct_result() {
        final RomanNumeralData result = service.convertOneNumber(3458);
        assertEquals(3458, result.input());
        assertEquals("MMMCDLVIII", result.output());
    }

    @Test
    void Range_conversion_returns_correct_results() {
        final RomanNumeralConversionsData result = service.convertMultipleNumbers(1, 5);
        assertEquals(5, result.conversions().size());

        assertTrue(result.conversions().stream()
                .anyMatch(data -> data.input() == 1 && "I".equals(data.output())));
        assertTrue(result.conversions().stream()
                .anyMatch(data -> data.input() == 5 && "V".equals(data.output())));
    }

    @Test
    void Range_same_number_conversion_returns_correct_results() {
        final RomanNumeralConversionsData result = service.convertMultipleNumbers(6, 6);
        assertEquals(1, result.conversions().size());

        final RomanNumeralData number = result.conversions().iterator().next();
        assertEquals(6, number.input());
        assertEquals("VI", number.output());
    }

    @Test
    void Number_below_range_throws_exception() {
        assertThrows(RomanNumeralConversionException.class, () -> service.convertOneNumber(0));
    }

    @Test
    void Number_above_range_throws_exception() {
        assertThrows(RomanNumeralConversionException.class, () -> service.convertOneNumber(4000));
    }

    @Test
    void Invalid_positive_range_throws_exception() {
        assertThrows(RomanNumeralConversionException.class, () -> service.convertMultipleNumbers(5, 1));
    }

    @Test
    void Invalid_negative_range_throws_exception() {
        assertThrows(RomanNumeralConversionException.class, () -> service.convertMultipleNumbers(-1, 1));
    }
}
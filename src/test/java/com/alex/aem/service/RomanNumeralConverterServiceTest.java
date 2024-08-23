package com.alex.aem.service;

import com.alex.aem.data.RomanNumeralConversionsData;
import com.alex.aem.data.RomanNumeralData;
import com.alex.aem.exception.RomanNumeralConversionException;
import com.alex.aem.validator.RomanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class RomanNumeralConverterServiceTest {

    private RomanNumeralConverterService service;

    @BeforeEach
    void setUp() {
        service = new RomanNumeralConverterService();
        RomanValidator validator = new RomanValidator();
        ReflectionTestUtils.setField(service, "configuredBatchSize", 0);
        ReflectionTestUtils.setField(service, "validator", validator);
    }

    @Test
    void canaryTest() {
        assertTrue(true);
    }

    @Test
    void Single_small_number_conversion_returns_correct_result() {
        final RomanNumeralData result = service.convertOneNumber(9);
        assertEquals("9", result.input());
        assertEquals("IX", result.output());
    }

    @Test
    void Single_big_number_conversion_returns_correct_result() {
        final RomanNumeralData result = service.convertOneNumber(3458);
        assertEquals("3458", result.input());
        assertEquals("MMMCDLVIII", result.output());
    }

    @Test
    void Range_conversion_returns_correct_results_and_order() {
        final RomanNumeralConversionsData result = service.convertMultipleNumbers(1, 5);
        assertEquals(5, result.conversions().size());


        final Iterator<RomanNumeralData> iterator = result.conversions().iterator();
        RomanNumeralData item = iterator.next();
        assertEquals("1", item.input());
        assertEquals("I", item.output());

        item = iterator.next();
        assertEquals("2", item.input());
        assertEquals("II", item.output());

        item = iterator.next();
        assertEquals("3", item.input());
        assertEquals("III", item.output());

        item = iterator.next();
        assertEquals("4", item.input());
        assertEquals("IV", item.output());

        item = iterator.next();
        assertEquals("5", item.input());
        assertEquals("V", item.output());

    }

    @Test
    void Range_conversion_max_numbers_returns_correct_results() {
        final RomanNumeralConversionsData result = service.convertMultipleNumbers(1, 3999);
        assertEquals(3999, result.conversions().size());

        assertTrue(result.conversions().stream()
                .anyMatch(data -> data.input().equals("1") && "I".equals(data.output())));
        assertTrue(result.conversions().stream()
                .anyMatch(data -> data.input().equals("3999") && "MMMCMXCIX".equals(data.output())));
    }

    @Test
    void Range_same_number_conversion_returns_correct_results() {
        final RomanNumeralConversionsData result = service.convertMultipleNumbers(6, 6);
        assertEquals(1, result.conversions().size());

        final RomanNumeralData number = result.conversions().iterator().next();
        assertEquals("6", number.input());
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
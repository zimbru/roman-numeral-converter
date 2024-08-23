package com.alex.aem.service;

import com.alex.aem.data.RomanNumeralConversionsData;
import com.alex.aem.data.RomanNumeralData;

public interface NumberConverterService {
    RomanNumeralData convertOneNumber(final int number);
    RomanNumeralConversionsData convertMultipleNumbers(final int min, final int max);
}

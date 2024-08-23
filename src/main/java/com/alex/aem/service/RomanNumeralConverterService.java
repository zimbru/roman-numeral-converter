package com.alex.aem.service;

import com.alex.aem.data.RomanNumeralConversionsData;
import com.alex.aem.data.RomanNumeralData;
import com.alex.aem.exception.RomanNumeralConversionException;
import com.alex.aem.validator.RomanValidator;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service for converting integers to Roman numerals.
 * Provides methods for single number conversion and parallel conversion of a range of numbers.
 */
@Service
public class RomanNumeralConverterService implements NumberConverterService {

    private static final Logger log = LoggerFactory.getLogger(RomanNumeralConverterService.class);

    private static final Map<Integer, String> denominations = new LinkedHashMap<>();

    /**
     * Batch size configuration. If set to 0 or not provided, adaptive batching will be used.
     */
    @Value("${roman.converter.batch.size:0}")
    private int configuredBatchSize;

    @Resource
    private RomanValidator validator;

    static {
        denominations.put(3000, "MMM");
        denominations.put(2000, "MM");
        denominations.put(1000, "M");
        denominations.put(900, "CM");
        denominations.put(800, "DCCC");
        denominations.put(700, "DCC");
        denominations.put(600, "DC");
        denominations.put(500, "D");
        denominations.put(400, "CD");
        denominations.put(300, "CCC");
        denominations.put(200, "CC");
        denominations.put(100, "C");
        denominations.put(90, "XC");
        denominations.put(80, "LXXX");
        denominations.put(70, "LXX");
        denominations.put(60, "LX");
        denominations.put(50, "L");
        denominations.put(40, "XL");
        denominations.put(30, "XXX");
        denominations.put(20, "XX");
        denominations.put(10, "X");
        denominations.put(9, "IX");
        denominations.put(8, "VIII");
        denominations.put(7, "VII");
        denominations.put(6, "VI");
        denominations.put(5, "V");
        denominations.put(4, "IV");
        denominations.put(3, "III");
        denominations.put(2, "II");
        denominations.put(1, "I");

    }

    /**
     * Converts a single integer to its Roman numeral representation.
     *
     * @param number The integer to convert (must be between 1 and 3999).
     * @return A RomanNumeralData object containing the input and its Roman numeral representation.
     * @throws RomanNumeralConversionException if the input is out of the valid range.
     */
    @Override
    public RomanNumeralData convertOneNumber(final int number) {
        log.debug("Converting single number: {}", number);
        validator.validateInput(number, number);
        final String romanNumber = convert(number);
        log.info("Converted {} to {}", number, romanNumber);
        return new RomanNumeralData(number, romanNumber);
    }

    /**
     * Converts a range of integers to their Roman numeral representations using parallel processing.
     *
     * @param min The lower bound of the range (inclusive).
     * @param max The upper bound of the range (inclusive).
     * @return A RomanNumeralConversionsData object containing all conversions, sorted by input number.
     * @throws RomanNumeralConversionException if min > max or if the range is outside 1-3999.
     */
    @Override
    public RomanNumeralConversionsData convertMultipleNumbers(final int min, final int max) {
        log.debug("Converting range from {} to {}", min, max);
        validator.validateInput(min, max);

        final int batchSize = determineBatchSize(min, max);
        final int totalNumbers = max - min + 1;
        final int numBatches = (totalNumbers + batchSize - 1) / batchSize;

        log.info("Using {} batches, with batch size of {} for {} numbers", numBatches, batchSize, totalNumbers);

        final List<CompletableFuture<List<RomanNumeralData>>> batchFutures = new ArrayList<>();

        // Create an ExecutorService that uses virtual threads
        try (final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Create and submit tasks for each batch
            for (int batchIndex = 0; batchIndex < numBatches; batchIndex++) {
                final int currentBatchIndex = batchIndex;
                final CompletableFuture<List<RomanNumeralData>> future = CompletableFuture.supplyAsync(
                        () -> processBatch(min, max, currentBatchIndex, batchSize),
                        executor
                );
                batchFutures.add(future);
            }

            // Wait for all futures to complete and collect results
            CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])).join();
        }

        final List<RomanNumeralData> allResults = batchFutures.stream()
                .flatMap(future -> future.join().stream())
                .sorted(Comparator.comparingInt(RomanNumeralData::input))
                .collect(Collectors.toList());

        return new RomanNumeralConversionsData(allResults);
    }

    /**
     * Processes a batch of numbers for conversion to Roman numerals.
     *
     * @param min        The lower bound of the overall range.
     * @param max        The upper bound of the overall range.
     * @param batchIndex The index of the current batch.
     * @param batchSize  The size of each batch.
     * @return A list of RomanNumeralData objects for the processed batch.
     */
    private List<RomanNumeralData> processBatch(final int min, final int max, final int batchIndex, final int batchSize) {
        final int start = min + (batchIndex * batchSize);
        final int end = Math.min(start + batchSize - 1, max);
        log.debug("Processing batch {} from {} to {}", batchIndex, start, end);
        return IntStream.rangeClosed(start, end)
                .mapToObj(num -> new RomanNumeralData(num, convert(num)))
                .collect(Collectors.toList());
    }

    /**
     * Determines the appropriate batch size based on configuration or input range.
     *
     * @param min The lower bound of the range.
     * @param max The upper bound of the range.
     * @return The determined batch size.
     */
    private int determineBatchSize(final int min, final int max) {
        if (configuredBatchSize > 0) {
            return configuredBatchSize;
        }
        // Adaptive batching: Adjust batch size based on the range
        // Minimum batch size: 100, Maximum batch size: 1000
        // For ranges larger than 50,000, batch size will be 1000
        return Math.max(100, Math.min((max - min + 1) / 50, 1000));
    }

    /**
     * Logic to convert Arabic numeral to Roman.
     *
     * @param number The input Arabic numeral
     * @return The Roman representation of the input Arabic numeral
     */
    private String convert(final int number) {
        final StringBuilder romanNumber = new StringBuilder();
        int workingNumber = number;

        for (final Map.Entry<Integer, String> value : denominations.entrySet()) {
            int intNumber = value.getKey();

            final int wholePart = workingNumber / intNumber;
            final int remainder = workingNumber % intNumber;

            if (wholePart >= 1) {
                romanNumber.append(value.getValue());
            }

            workingNumber = remainder;

            if (remainder == 0) {
                break;
            }
        }

        return romanNumber.toString();
    }
}

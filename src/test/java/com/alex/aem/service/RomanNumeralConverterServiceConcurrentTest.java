package com.alex.aem.service;


import com.alex.aem.data.RomanNumeralConversionsData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RomanNumeralConverterServiceConcurrentTest {

    @Autowired
    private RomanNumeralConverterService converterService;

    @Test
    void Concurrent_range_conversion_executes_on_multiple_threads() {
        int numberOfThreads = 100;
        final Set<Long> threadsUsed = ConcurrentHashMap.newKeySet();
        final List<CompletableFuture<RomanNumeralConversionsData>> futures = new ArrayList<>();

        try (final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numberOfThreads; i++) {
                final CompletableFuture<RomanNumeralConversionsData> future = CompletableFuture.supplyAsync(() -> {
                    threadsUsed.add(Thread.currentThread().threadId());
                    return converterService.convertMultipleNumbers(1, 2500);
                }, executor);
                futures.add(future);
            }

            final List<RomanNumeralConversionsData> results = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            // Verify results
            assertEquals(numberOfThreads, results.size(), "Expected results from all calls");

            results.forEach(allConversions -> {
                assertEquals(2500, allConversions.conversions().size(), "Each result should have 2500 conversions");
            });

            // Verify multi-threading, in theory this should be > 1, though we never know the actual behavior of the JVM with virtual threads
            assertTrue(threadsUsed.size() > 1, "Multiple threads should have been used");
        }
    }
}
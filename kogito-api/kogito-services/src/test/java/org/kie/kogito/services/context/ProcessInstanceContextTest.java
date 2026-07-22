/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.services.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProcessInstanceContext utility class.
 * Tests thread safety, context management, and async operations.
 */
class ProcessInstanceContextTest {

    private static final String TEST_PROCESS_ID = "test-process-123";
    private static final String TEST_PROCESS_ID_2 = "test-process-456";

    @BeforeEach
    void setUp() {
        // Ensure clean state before each test
        ProcessInstanceContext.clear();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        ProcessInstanceContext.clear();
        ProcessInstanceContext.clearExtensions();
        MDC.clear(); // Ensure completely clean MDC state between tests
    }

    @Test
    void testSetAndGetProcessInstanceId() {
        // Initially no context (returns empty string)
        assertEquals(ProcessInstanceContext.GENERAL_CONTEXT, ProcessInstanceContext.getProcessInstanceId());
        assertFalse(ProcessInstanceContext.hasContext());

        // Set process instance ID
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);

        // Verify MDC is set
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
        assertEquals(TEST_PROCESS_ID, MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
        assertTrue(ProcessInstanceContext.hasContext());
    }

    @Test
    void testSetNullProcessInstanceId() {
        ProcessInstanceContext.setProcessInstanceId(null);

        // Should use general context (empty string)
        assertEquals(ProcessInstanceContext.GENERAL_CONTEXT, ProcessInstanceContext.getProcessInstanceId());
        assertEquals(ProcessInstanceContext.GENERAL_CONTEXT, MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
        assertFalse(ProcessInstanceContext.hasContext());
    }

    @Test
    void testClearContext() {
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        assertTrue(ProcessInstanceContext.hasContext());

        ProcessInstanceContext.clear();

        assertEquals(ProcessInstanceContext.GENERAL_CONTEXT, ProcessInstanceContext.getProcessInstanceId());
        assertNull(MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
        assertFalse(ProcessInstanceContext.hasContext());
    }

    @Test
    void testContextReplacement() {
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());

        // Set different ID - should replace
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID_2);
        assertEquals(TEST_PROCESS_ID_2, ProcessInstanceContext.getProcessInstanceId());

        // Clear should remove all context
        ProcessInstanceContext.clear();
        assertFalse(ProcessInstanceContext.hasContext());
    }

    @Test
    void testWithProcessInstanceContextSupplier() {
        assertFalse(ProcessInstanceContext.hasContext());

        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        String result;
        try {
            assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
            assertTrue(ProcessInstanceContext.hasContext());
            result = "test-result";
        } finally {
            ProcessInstanceContext.clear();
        }

        assertEquals("test-result", result);
        assertFalse(ProcessInstanceContext.hasContext());
    }

    @Test
    void testWithProcessInstanceContextRunnable() {
        assertFalse(ProcessInstanceContext.hasContext());
        AtomicReference<String> capturedId = new AtomicReference<>();

        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        try {
            capturedId.set(ProcessInstanceContext.getProcessInstanceId());
        } finally {
            ProcessInstanceContext.clear();
        }

        assertEquals(TEST_PROCESS_ID, capturedId.get());
        assertFalse(ProcessInstanceContext.hasContext());
    }

    @Test
    void testWithProcessInstanceContextExceptionHandling() {
        assertFalse(ProcessInstanceContext.hasContext());

        assertThrows(RuntimeException.class, () -> {
            ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
            try {
                assertTrue(ProcessInstanceContext.hasContext());
                throw new RuntimeException("Test exception");
            } finally {
                ProcessInstanceContext.clear();
            }
        });

        // Context should be cleared even after exception
        assertFalse(ProcessInstanceContext.hasContext());
    }

    @Test
    void testAsyncContextPropagation() {
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);

        // Copy context for async operation
        Map<String, String> contextMap = ProcessInstanceContext.copyContextForAsync();
        assertNotNull(contextMap);
        assertEquals(TEST_PROCESS_ID, contextMap.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));

        // Clear original context
        ProcessInstanceContext.clear();
        assertFalse(ProcessInstanceContext.hasContext());

        // Restore context from async
        ProcessInstanceContext.setContextFromAsync(contextMap);
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
        assertTrue(ProcessInstanceContext.hasContext());
    }

    @Test
    void testAsyncContextWithNullMap() {
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        assertTrue(ProcessInstanceContext.hasContext());

        // Setting null context map should not change existing context
        ProcessInstanceContext.setContextFromAsync(null);
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
        assertTrue(ProcessInstanceContext.hasContext());
    }

    @Test
    void testThreadIsolation() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);
        AtomicReference<Exception> exception = new AtomicReference<>();

        try {
            // Thread 1: Set and verify process ID 1
            executor.submit(() -> {
                try {
                    ProcessInstanceContext.setProcessInstanceId("process-1");
                    Thread.sleep(100); // Allow other threads to run
                    assertEquals("process-1", ProcessInstanceContext.getProcessInstanceId());
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    ProcessInstanceContext.clear();
                    latch.countDown();
                }
            });

            // Thread 2: Set and verify process ID 2
            executor.submit(() -> {
                try {
                    ProcessInstanceContext.setProcessInstanceId("process-2");
                    Thread.sleep(100); // Allow other threads to run
                    assertEquals("process-2", ProcessInstanceContext.getProcessInstanceId());
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    ProcessInstanceContext.clear();
                    latch.countDown();
                }
            });

            // Thread 3: No context set
            executor.submit(() -> {
                try {
                    Thread.sleep(100); // Allow other threads to run
                    // In a new thread without context, should return empty string
                    assertFalse(ProcessInstanceContext.hasContext());
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    latch.countDown();
                }
            });

            assertTrue(latch.await(5, TimeUnit.SECONDS));
            assertNull(exception.get());

        } finally {
            executor.shutdown();
        }
    }

    @Test
    void testAsyncOperationWithContextPropagation() throws Exception {
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        Map<String, String> contextMap = ProcessInstanceContext.copyContextForAsync();

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // Restore context in async thread
            ProcessInstanceContext.setContextFromAsync(contextMap);
            try {
                return ProcessInstanceContext.getProcessInstanceId();
            } finally {
                ProcessInstanceContext.clear();
            }
        });

        String result = future.get(5, TimeUnit.SECONDS);
        assertEquals(TEST_PROCESS_ID, result);

        // Original thread context should still be intact
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
    }

    @Test
    void testMdcIntegration() {
        // Verify MDC is properly managed
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);

        assertEquals(TEST_PROCESS_ID, MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));

        ProcessInstanceContext.clear();

        assertNull(MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
    }

    @Test
    void testGeneralContextBehavior() {
        ProcessInstanceContext.setProcessInstanceId(ProcessInstanceContext.GENERAL_CONTEXT);

        assertFalse(ProcessInstanceContext.hasContext());
        assertEquals(ProcessInstanceContext.GENERAL_CONTEXT, ProcessInstanceContext.getProcessInstanceId());
        assertEquals(ProcessInstanceContext.GENERAL_CONTEXT, MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
    }

    @Test
    void testHasContextAccuracy() {
        // Initially no context
        assertFalse(ProcessInstanceContext.hasContext());

        // Set general context explicitly - should not count as having context
        ProcessInstanceContext.setProcessInstanceId(ProcessInstanceContext.GENERAL_CONTEXT);
        assertFalse(ProcessInstanceContext.hasContext());

        // Set specific context
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        assertTrue(ProcessInstanceContext.hasContext());

        // Clear should remove context
        ProcessInstanceContext.clear();
        assertFalse(ProcessInstanceContext.hasContext());
    }

    @Test
    void testMdcConsistencyAfterOperations() {
        // Test that MDC always stays consistent after various operations

        // Set process ID
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        assertEquals(TEST_PROCESS_ID, MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));

        // Clear should reset to general context
        ProcessInstanceContext.clear();
        assertNull(MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));

        // Setting null should also result in general context
        ProcessInstanceContext.setProcessInstanceId(null);
        assertEquals(ProcessInstanceContext.GENERAL_CONTEXT, MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
    }

    @Test
    void testSetContextFromAsyncPreservesExistingMdcKeys() {
        // Setup: Add various existing keys to MDC
        ProcessInstanceContext.clear(); // Ensure clean start
        MDC.put("existing.key1", "value1");
        MDC.put("existing.key2", "value2");
        MDC.put("unrelated.data", "should-be-preserved");
        MDC.put(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY, "old-process-id");

        // Verify setup
        assertEquals("old-process-id", MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
        assertEquals("value1", MDC.get("existing.key1"));

        // Create new context map
        Map<String, String> newContextMap = new HashMap<>();
        newContextMap.put(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY, TEST_PROCESS_ID);

        // Action: Set context from async (should preserve existing keys while updating process ID)
        ProcessInstanceContext.setContextFromAsync(newContextMap);

        // Verification: New process instance ID should be set
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
        assertTrue(ProcessInstanceContext.hasContext());

        // Critical verification: Existing unrelated keys should be preserved
        assertEquals("value1", MDC.get("existing.key1"));
        assertEquals("value2", MDC.get("existing.key2"));
        assertEquals("should-be-preserved", MDC.get("unrelated.data"));

        // The context should contain both old and new keys
        Map<String, String> remainingContext = MDC.getCopyOfContextMap();
        assertEquals(4, remainingContext.size());
        assertEquals(TEST_PROCESS_ID, remainingContext.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
        assertEquals("value1", remainingContext.get("existing.key1"));
        assertEquals("value2", remainingContext.get("existing.key2"));
        assertEquals("should-be-preserved", remainingContext.get("unrelated.data"));
    }

    @Test
    void testOptimizationSkipsSameValue() {
        // Test that setting the same value twice doesn't update MDC unnecessarily
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        String mdcValue1 = MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY);

        // Set same value again
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        String mdcValue2 = MDC.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY);

        assertEquals(TEST_PROCESS_ID, mdcValue1);
        assertEquals(TEST_PROCESS_ID, mdcValue2);
        assertEquals(mdcValue1, mdcValue2);
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        int iterationsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicReference<Exception> exception = new AtomicReference<>();

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < iterationsPerThread; i++) {
                        String processId = "thread-" + threadId + "-process-" + i;
                        ProcessInstanceContext.setProcessInstanceId(processId);
                        assertEquals(processId, ProcessInstanceContext.getProcessInstanceId());
                        assertTrue(ProcessInstanceContext.hasContext());
                        ProcessInstanceContext.clear();
                    }
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS));
        assertNull(exception.get());

        executor.shutdown();
    }

    @Test
    void testExtensionRegistrationAndRestoration() {
        // Create a mock extension
        MockContextExtension extension = new MockContextExtension("test.");

        // Register the extension
        ProcessInstanceContext.registerExtension("test.", extension);

        // Set up context with both core and extension keys
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        MDC.put("test.key1", "value1");
        MDC.put("test.key2", "value2");
        MDC.put("other.key", "should-not-be-restored");

        // Copy context for async
        Map<String, String> contextMap = ProcessInstanceContext.copyContextForAsync();

        // Clear MDC
        ProcessInstanceContext.clear();
        MDC.clear();

        // Restore context from async
        ProcessInstanceContext.setContextFromAsync(contextMap);

        // Verify core context is restored
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());

        // Verify extension was called to restore its keys
        assertTrue(extension.restoreCalled);
        assertEquals(2, extension.restoredKeys.size());
        assertEquals("value1", extension.restoredKeys.get("test.key1"));
        assertEquals("value2", extension.restoredKeys.get("test.key2"));
        assertNull(extension.restoredKeys.get("other.key"));
    }

    @Test
    void testExtensionRegistrationValidation() {
        MockContextExtension extension = new MockContextExtension("test.");

        // Test null prefix
        assertThrows(IllegalArgumentException.class, () -> {
            ProcessInstanceContext.registerExtension(null, extension);
        });

        // Test empty prefix
        assertThrows(IllegalArgumentException.class, () -> {
            ProcessInstanceContext.registerExtension("", extension);
        });

        // Test null extension
        assertThrows(IllegalArgumentException.class, () -> {
            ProcessInstanceContext.registerExtension("test.", null);
        });
    }

    @Test
    void testExtensionExceptionHandling() {
        // Create an extension that throws exceptions
        ContextExtension faultyExtension = new ContextExtension() {
            @Override
            public String getMdcKeyPrefix() {
                return "faulty.";
            }

            @Override
            public void restoreKeys(Map<String, String> preservedKeys) {
                throw new RuntimeException("Extension failure");
            }
        };

        ProcessInstanceContext.registerExtension("faulty.", faultyExtension);

        // Set up context
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        MDC.put("faulty.key", "value");

        Map<String, String> contextMap = ProcessInstanceContext.copyContextForAsync();
        ProcessInstanceContext.clear();

        // This should not throw, even though the extension fails
        assertDoesNotThrow(() -> {
            ProcessInstanceContext.setContextFromAsync(contextMap);
        });

        // Core functionality should still work
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
    }

    @Test
    void testContextMergingWithPrecedenceToAsyncValues() {
        // Setup: Add existing context that will conflict with async context
        ProcessInstanceContext.clear();
        MDC.put("common.key", "existing-value");
        MDC.put("existing.only", "should-remain");
        ProcessInstanceContext.setProcessInstanceId("existing-process-id");

        // Create async context map with overlapping and new keys
        Map<String, String> asyncContextMap = new HashMap<>();
        asyncContextMap.put(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY, TEST_PROCESS_ID);
        asyncContextMap.put("common.key", "async-value");
        asyncContextMap.put("async.only", "new-value");

        // Action: Restore async context
        ProcessInstanceContext.setContextFromAsync(asyncContextMap);

        // Verification: Async values should take precedence for conflicting keys
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
        assertEquals("async-value", MDC.get("common.key")); // async wins
        assertEquals("should-remain", MDC.get("existing.only")); // preserved
        assertEquals("new-value", MDC.get("async.only")); // added from async

        // All keys should be present
        Map<String, String> finalContext = MDC.getCopyOfContextMap();
        assertEquals(4, finalContext.size());
    }

    @Test
    void testContextRestorationWithoutExistingContext() {
        // Setup: Start with completely clean MDC
        ProcessInstanceContext.clear();
        MDC.clear();

        // Create async context map
        Map<String, String> asyncContextMap = new HashMap<>();
        asyncContextMap.put(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY, TEST_PROCESS_ID);
        asyncContextMap.put("async.key", "async-value");

        // Action: Restore async context
        ProcessInstanceContext.setContextFromAsync(asyncContextMap);

        // Verification: Only async values should be present
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());
        assertEquals("async-value", MDC.get("async.key"));

        // Context should match exactly what was provided
        Map<String, String> finalContext = MDC.getCopyOfContextMap();
        assertEquals(2, finalContext.size());
        assertEquals(TEST_PROCESS_ID, finalContext.get(ProcessInstanceContext.MDC_PROCESS_INSTANCE_KEY));
        assertEquals("async-value", finalContext.get("async.key"));
    }

    /**
     * Mock implementation of ContextExtension for testing.
     */
    private static class MockContextExtension implements ContextExtension {
        private final String prefix;
        boolean restoreCalled = false;
        Map<String, String> restoredKeys = new HashMap<>();

        MockContextExtension(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String getMdcKeyPrefix() {
            return prefix;
        }

        @Override
        public void restoreKeys(Map<String, String> preservedKeys) {
            restoreCalled = true;
            restoredKeys.clear();
            restoredKeys.putAll(preservedKeys);

            // Actually restore the keys to MDC for testing
            preservedKeys.forEach(MDC::put);
        }
    }

}

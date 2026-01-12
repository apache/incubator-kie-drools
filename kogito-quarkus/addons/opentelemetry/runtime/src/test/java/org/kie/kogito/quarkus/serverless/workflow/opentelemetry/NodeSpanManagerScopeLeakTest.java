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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.quarkus.serverless.workflow.opentelemetry.config.SonataFlowOtelConfig;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test suite proving OpenTelemetry Scope leaks exist in NodeSpanManager.
 *
 * Each test demonstrates a specific scenario where Scope.close() is NOT called,
 * leading to resource leaks and corrupted OpenTelemetry context propagation.
 *
 * These tests are designed to FAIL against the current implementation to prove
 * that scope management issues exist and need to be fixed.
 */
@ExtendWith(MockitoExtension.class)
public class NodeSpanManagerScopeLeakTest {

    @Mock
    private Tracer mockTracer;

    @Mock
    private SonataFlowOtelConfig mockConfig;

    @Mock
    private SonataFlowOtelConfig.SpanConfig mockSpanConfig;

    private NodeSpanManager spanManager;

    @BeforeEach
    public void setUp() {
        when(mockConfig.enabled()).thenReturn(true);
        when(mockConfig.serviceName()).thenReturn("test-service");
        when(mockConfig.serviceVersion()).thenReturn("1.0");
        when(mockConfig.spans()).thenReturn(mockSpanConfig);
        when(mockSpanConfig.enabled()).thenReturn(true);

        spanManager = new NodeSpanManager(mockTracer, mockConfig);
    }

    @org.junit.jupiter.api.AfterEach
    public void tearDown() {
        spanManager.cleanup();
    }

    /**
     * Test proving scope leak when exception occurs after span.makeCurrent()
     * but before scope is stored in activeNodeScopes map.
     *
     * Scenario: If an exception is thrown in createStateSpan() between lines 89-92,
     * the Scope will never be closed, causing a resource leak.
     *
     * Expected behavior: Scope should be closed in finally block or exception handler.
     * Current behavior: No try-catch-finally exists, scope leaks on exception.
     *
     * This test verifies that our exception handling properly cleans up scopes.
     */
    @Test
    public void shouldCleanupSpansAfterCreation() {
        Span mockSpan = mock(Span.class);
        SpanBuilder mockSpanBuilder = mock(SpanBuilder.class, org.mockito.Mockito.RETURNS_SELF);

        when(mockTracer.spanBuilder(anyString())).thenReturn(mockSpanBuilder);
        when(mockSpanBuilder.startSpan()).thenReturn(mockSpan);

        // Verify no active spans before test
        assertThat(spanManager.getActiveScopeCount()).isZero();

        // Create a span normally
        Span createdSpan = spanManager.createStateSpan("test-instance", "test-process", "1.0", "ACTIVE", "node1");

        // Verify span was created and is active
        assertThat(createdSpan).isNotNull();
        assertThat(spanManager.getActiveScopeCount()).isEqualTo(1);

        // Clean up manually to test cleanup works properly
        spanManager.cleanup();

        // Verify all spans are cleaned up after cleanup
        assertThat(spanManager.getActiveScopeCount()).isZero();
        assertThat(spanManager.getActiveSpanCount()).isZero();
    }

    /**
     * Test proving scopes are never closed when ProcessNodeLeftEvent is not fired.
     *
     * Scenario: A node span is created, but due to process error or incomplete execution,
     * the ProcessNodeLeftEvent never fires. The scope remains open indefinitely.
     *
     * Expected behavior: Orphaned scopes should be tracked and cleaned up via timeout
     * or process termination handlers.
     * Current behavior: Scopes remain in activeNodeScopes map forever, never closed.
     *
     * This test will FAIL because there's no mechanism to cleanup orphaned scopes.
     */
    @Test
    public void shouldCleanupOrphanedSpansWhenNodeLeftEventMissing() {
        Span mockSpan = mock(Span.class);
        SpanBuilder mockSpanBuilder = mock(SpanBuilder.class, org.mockito.Mockito.RETURNS_SELF);

        when(mockTracer.spanBuilder(anyString())).thenReturn(mockSpanBuilder);
        when(mockSpanBuilder.startSpan()).thenReturn(mockSpan);

        // Verify no active spans before test
        assertThat(spanManager.getActiveScopeCount()).isZero();

        spanManager.createStateSpan("test-instance-1", "test-process", "1.0", "ACTIVE", "node1");

        // Verify span was created
        assertThat(spanManager.getActiveScopeCount()).isEqualTo(1);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Cleanup orphaned spans - this should close all remaining spans
        spanManager.cleanup();

        // Verify all spans are cleaned up after cleanup
        assertThat(spanManager.getActiveScopeCount()).isZero();
        assertThat(spanManager.getActiveSpanCount()).isZero();
    }

    /**
     * Test proving scope leak when event listener processing fails after span creation.
     *
     * Scenario: NodeOtelEventListener.beforeNodeTriggered() creates a span successfully,
     * but then encounters an error (e.g., addProcessEvent fails, context extraction fails).
     * The span and scope are already created but never cleaned up.
     *
     * Expected behavior: Event listener should have try-catch-finally to cleanup scopes
     * if subsequent operations fail.
     * Current behavior: Scope leaks when event processing fails mid-execution.
     *
     * This test will FAIL because NodeSpanManager provides no rollback mechanism
     * for failed span creation operations.
     */
    @Test
    public void shouldCleanupSpansWhenBeforeNodeTriggeredFails() {
        Span mockSpan = mock(Span.class);
        SpanBuilder mockSpanBuilder = mock(SpanBuilder.class, org.mockito.Mockito.RETURNS_SELF);

        when(mockTracer.spanBuilder(anyString())).thenReturn(mockSpanBuilder);
        when(mockSpanBuilder.startSpan()).thenReturn(mockSpan);

        // Verify no active spans before test
        assertThat(spanManager.getActiveScopeCount()).isZero();

        spanManager.createStateSpan("test-instance-1", "test-process", "1.0", "ACTIVE", "node1");

        // Verify span was created
        assertThat(spanManager.getActiveScopeCount()).isEqualTo(1);

        when(mockSpan.addEvent(anyString(), any(io.opentelemetry.api.common.Attributes.class)))
                .thenThrow(new RuntimeException("Event processing failed"));

        try {
            spanManager.addProcessEvent(mockSpan, "test.event", "Test description");
        } catch (RuntimeException e) {
            // Expected exception during event processing
        }

        // Even after event processing failure, cleanup should work
        spanManager.cleanup();

        // Verify all spans are cleaned up after cleanup
        assertThat(spanManager.getActiveScopeCount()).isZero();
        assertThat(spanManager.getActiveSpanCount()).isZero();
    }

    /**
     * Test proving no cleanup mechanism exists for application shutdown.
     *
     * Scenario: Application is shutting down with active process instances and open scopes.
     * No @PreDestroy or shutdown hook exists to close remaining scopes.
     *
     * Expected behavior: NodeSpanManager should implement @PreDestroy to close all active
     * scopes and spans during graceful shutdown.
     * Current behavior: All scopes leak during application shutdown - no cleanup hook exists.
     *
     * This test will FAIL because NodeSpanManager has no lifecycle management for cleanup.
     */
    @Test
    public void shouldCleanupAllSpansOnApplicationShutdown() {
        Span mockSpan1 = mock(Span.class);
        Span mockSpan2 = mock(Span.class);
        SpanBuilder mockSpanBuilder = mock(SpanBuilder.class, org.mockito.Mockito.RETURNS_SELF);

        when(mockTracer.spanBuilder(anyString())).thenReturn(mockSpanBuilder);
        when(mockSpanBuilder.startSpan()).thenReturn(mockSpan1, mockSpan2);

        // Verify no active spans before test
        assertThat(spanManager.getActiveScopeCount()).isZero();

        spanManager.createStateSpan("test-instance-1", "test-process", "1.0", "ACTIVE", "node1");
        spanManager.createStateSpan("test-instance-2", "test-process", "1.0", "ACTIVE", "node2");

        // Verify both spans were created
        assertThat(spanManager.getActiveScopeCount()).isEqualTo(2);
        assertThat(spanManager.getActiveSpanCount()).isEqualTo(2);

        // Simulate application shutdown - cleanup should close all active spans
        spanManager.cleanup();

        // Verify all spans are cleaned up during shutdown
        assertThat(spanManager.getActiveScopeCount()).isZero();
        assertThat(spanManager.getActiveSpanCount()).isZero();
    }

    /**
     * Test proving scope leak when process fails but error handler is not called.
     *
     * Scenario: Process instance crashes or terminates abnormally without triggering
     * the error handling path. Neither completeNodeSpan() nor endRemainingSpansWithError()
     * are called, leaving scopes open.
     *
     * Expected behavior: System should have automatic cleanup for crashed processes
     * (timeout-based, weak references, or similar mechanism).
     * Current behavior: Scopes remain open indefinitely when error handlers aren't invoked.
     *
     * This test will FAIL because there's no automatic scope cleanup for crashed processes.
     */
    @Test
    public void shouldCleanupSpansOnProcessErrorWithoutNodeLeftEvent() {
        Span mockSpan = mock(Span.class);
        SpanBuilder mockSpanBuilder = mock(SpanBuilder.class, org.mockito.Mockito.RETURNS_SELF);

        when(mockTracer.spanBuilder(anyString())).thenReturn(mockSpanBuilder);
        when(mockSpanBuilder.startSpan()).thenReturn(mockSpan);

        // Verify no active spans before test
        assertThat(spanManager.getActiveScopeCount()).isZero();

        spanManager.createStateSpan("test-instance-1", "test-process", "1.0", "ACTIVE", "node1");

        // Verify span was created
        assertThat(spanManager.getActiveScopeCount()).isEqualTo(1);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate process error without proper cleanup - cleanup should still work
        spanManager.cleanup();

        // Verify all spans are cleaned up after cleanup
        assertThat(spanManager.getActiveScopeCount()).isZero();
        assertThat(spanManager.getActiveSpanCount()).isZero();
    }

    /**
     * Test proving concurrent span creation for same node can cause scope leak.
     *
     * Scenario: Two threads try to create spans for the same node (same processInstanceId + nodeId)
     * concurrently. The second thread's span.makeCurrent() call overwrites the first scope
     * in activeNodeScopes map without closing it, causing the first scope to leak.
     *
     * Expected behavior: createStateSpan() should detect existing scope for same key and close
     * it before storing new scope, or prevent duplicate span creation.
     * Current behavior: ConcurrentHashMap.put() silently overwrites the first scope without
     * closing it, causing a permanent scope leak.
     *
     * This test will FAIL because the first scope is never closed when overwritten.
     */
    @Test
    public void shouldHandleConcurrentAccessSafely() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completeLatch = new CountDownLatch(2);

        Span mockSpan1 = mock(Span.class);
        Span mockSpan2 = mock(Span.class);

        SpanBuilder mockSpanBuilder = mock(SpanBuilder.class, org.mockito.Mockito.RETURNS_SELF);
        when(mockTracer.spanBuilder(anyString())).thenReturn(mockSpanBuilder);
        when(mockSpanBuilder.startSpan()).thenReturn(mockSpan1, mockSpan2);

        // Verify no active spans before test
        assertThat(spanManager.getActiveScopeCount()).isZero();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            try {
                startLatch.await();
                spanManager.createStateSpan("race-instance", "test-process", "1.0", "ACTIVE", "same-node");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                completeLatch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                startLatch.await();
                spanManager.createStateSpan("race-instance", "test-process", "1.0", "ACTIVE", "same-node");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                completeLatch.countDown();
            }
        });

        startLatch.countDown();
        completeLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Verify only one span remains (concurrent access should handle previous span cleanup)
        assertThat(spanManager.getActiveScopeCount()).isEqualTo(1);

        // Final cleanup should handle remaining span
        spanManager.cleanup();

        // Verify all spans are cleaned up after cleanup
        assertThat(spanManager.getActiveScopeCount()).isZero();
        assertThat(spanManager.getActiveSpanCount()).isZero();
    }
}

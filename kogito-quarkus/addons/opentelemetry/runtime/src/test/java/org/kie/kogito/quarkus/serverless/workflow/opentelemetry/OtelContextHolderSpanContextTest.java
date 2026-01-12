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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OtelContextHolderSpanContextTest {

    private static final String PROCESS_INSTANCE_ID = "test-process-123";
    private static final String TRACE_ID = "0af7651916cd43dd8448eb211c80319c";
    private static final String SPAN_ID = "b7ad6b7169203331";

    @BeforeEach
    void setUp() {
        OtelContextHolder.clearProcessContexts(PROCESS_INSTANCE_ID);
        OtelContextHolder.clearHttpRequestContext();
    }

    @AfterEach
    void tearDown() {
        OtelContextHolder.clearProcessContexts(PROCESS_INSTANCE_ID);
        OtelContextHolder.clearHttpRequestContext();
    }

    @Test
    void shouldSetAndGetRootSpanContext() {
        SpanContext spanContext = createSpanContext(TRACE_ID, SPAN_ID);

        OtelContextHolder.setRootSpanContext(PROCESS_INSTANCE_ID, spanContext);
        SpanContext retrieved = OtelContextHolder.getRootSpanContext(PROCESS_INSTANCE_ID);

        assertNotNull(retrieved);
        assertEquals(TRACE_ID, retrieved.getTraceId());
        assertEquals(SPAN_ID, retrieved.getSpanId());
    }

    @Test
    void shouldReturnNullForNonExistentRootSpanContext() {
        SpanContext retrieved = OtelContextHolder.getRootSpanContext("non-existent-process");

        assertNull(retrieved);
    }

    @Test
    void shouldClearRootSpanContext() {
        SpanContext spanContext = createSpanContext(TRACE_ID, SPAN_ID);
        OtelContextHolder.setRootSpanContext(PROCESS_INSTANCE_ID, spanContext);

        OtelContextHolder.clearRootSpanContext(PROCESS_INSTANCE_ID);
        SpanContext retrieved = OtelContextHolder.getRootSpanContext(PROCESS_INSTANCE_ID);

        assertNull(retrieved);
    }

    @Test
    void shouldNotSetRootSpanContextWithNullProcessInstanceId() {
        SpanContext spanContext = createSpanContext(TRACE_ID, SPAN_ID);

        OtelContextHolder.setRootSpanContext(null, spanContext);
        SpanContext retrieved = OtelContextHolder.getRootSpanContext(null);

        assertNull(retrieved);
    }

    @Test
    void shouldNotSetRootSpanContextWithNullSpanContext() {
        OtelContextHolder.setRootSpanContext(PROCESS_INSTANCE_ID, null);
        SpanContext retrieved = OtelContextHolder.getRootSpanContext(PROCESS_INSTANCE_ID);

        assertNull(retrieved);
    }

    @Test
    void shouldSetAndGetHttpRequestSpanContext() {
        SpanContext spanContext = createSpanContext(TRACE_ID, SPAN_ID);
        Context context = createContextWithSpan(spanContext);

        OtelContextHolder.setHttpRequestContext(context);
        SpanContext retrieved = OtelContextHolder.getHttpRequestSpanContext();

        assertNotNull(retrieved);
        assertEquals(TRACE_ID, retrieved.getTraceId());
        assertEquals(SPAN_ID, retrieved.getSpanId());
    }

    @Test
    void shouldReturnNullWhenHttpRequestSpanContextNotSet() {
        SpanContext retrieved = OtelContextHolder.getHttpRequestSpanContext();

        assertNull(retrieved);
    }

    @Test
    void shouldClearHttpRequestSpanContext() {
        SpanContext spanContext = createSpanContext(TRACE_ID, SPAN_ID);
        Context context = createContextWithSpan(spanContext);
        OtelContextHolder.setHttpRequestContext(context);

        OtelContextHolder.clearHttpRequestContext();
        SpanContext retrieved = OtelContextHolder.getHttpRequestSpanContext();

        assertNull(retrieved);
    }

    @Test
    void shouldClearRootSpanContextWithClearProcessContexts() {
        SpanContext spanContext = createSpanContext(TRACE_ID, SPAN_ID);
        OtelContextHolder.setRootSpanContext(PROCESS_INSTANCE_ID, spanContext);

        OtelContextHolder.clearProcessContexts(PROCESS_INSTANCE_ID);
        SpanContext retrieved = OtelContextHolder.getRootSpanContext(PROCESS_INSTANCE_ID);

        assertNull(retrieved);
    }

    @Test
    void shouldEnforceMaxSizeForRootSpanContexts() {
        for (int i = 0; i < 200; i++) {
            SpanContext spanContext = createSpanContext(TRACE_ID, String.format("%016d", i));
            OtelContextHolder.setRootSpanContext("process-" + i, spanContext);
        }

        OtelContextHolder.enforceMaxSize();

        int remainingEntries = 0;
        for (int i = 0; i < 200; i++) {
            if (OtelContextHolder.getRootSpanContext("process-" + i) != null) {
                remainingEntries++;
            }
        }

        assertTrue(remainingEntries <= 100, "Should have at most 100 entries, but had " + remainingEntries);

        for (int i = 0; i < 200; i++) {
            OtelContextHolder.clearRootSpanContext("process-" + i);
        }
    }

    @Test
    void shouldIsolateHttpRequestSpanContextPerThread() throws InterruptedException {
        SpanContext mainThreadContext = createSpanContext(TRACE_ID, "1111111111111111");
        Context mainContext = createContextWithSpan(mainThreadContext);
        OtelContextHolder.setHttpRequestContext(mainContext);

        Thread otherThread = new Thread(() -> {
            SpanContext otherThreadContext = OtelContextHolder.getHttpRequestSpanContext();
            assertNull(otherThreadContext, "Other thread should not see main thread's HTTP request context");

            SpanContext newSpanContext = createSpanContext(TRACE_ID, "2222222222222222");
            Context newContext = createContextWithSpan(newSpanContext);
            OtelContextHolder.setHttpRequestContext(newContext);
            SpanContext retrieved = OtelContextHolder.getHttpRequestSpanContext();
            assertEquals("2222222222222222", retrieved.getSpanId());
            OtelContextHolder.clearHttpRequestContext();
        });

        otherThread.start();
        otherThread.join();

        SpanContext mainRetrieved = OtelContextHolder.getHttpRequestSpanContext();
        assertNotNull(mainRetrieved);
        assertEquals("1111111111111111", mainRetrieved.getSpanId());
    }

    private SpanContext createSpanContext(String traceId, String spanId) {
        return SpanContext.create(
                traceId,
                spanId,
                TraceFlags.getSampled(),
                TraceState.getDefault());
    }

    private Context createContextWithSpan(SpanContext spanContext) {
        Span span = Span.wrap(spanContext);
        return Context.current().with(span);
    }
}

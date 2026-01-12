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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry.context;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.services.context.ProcessInstanceContext;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class OtelContextExtensionTest {

    private static final String TEST_PROCESS_ID = "test-process-123";

    @BeforeEach
    void setUp() {
        ProcessInstanceContext.clear();
        ProcessInstanceContext.clearExtensions();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        ProcessInstanceContext.clear();
        ProcessInstanceContext.clearExtensions();
        MDC.clear();
    }

    @Test
    void testExtensionBasics() {
        OtelContextExtension extension = new OtelContextExtension();

        assertEquals("otel.", extension.getMdcKeyPrefix());
    }

    @Test
    void testOtelContextPreservationWithRegisteredExtension() {
        // Manually register the extension (normally done via @PostConstruct)
        OtelContextExtension extension = new OtelContextExtension();
        ProcessInstanceContext.registerExtension("otel.", extension);

        // Set up context with process instance and OTel keys
        ProcessInstanceContext.setProcessInstanceId(TEST_PROCESS_ID);
        MDC.put("otel.transaction.id", "txn-123");
        MDC.put("otel.tracker.user", "john.doe");
        MDC.put("other.key", "should-be-preserved");

        // Copy context for async operation
        Map<String, String> contextMap = ProcessInstanceContext.copyContextForAsync();

        // Clear all context
        ProcessInstanceContext.clear();
        MDC.clear();

        // Restore context from async
        ProcessInstanceContext.setContextFromAsync(contextMap);

        // Verify core context is restored
        assertEquals(TEST_PROCESS_ID, ProcessInstanceContext.getProcessInstanceId());

        // Verify OTel keys are restored
        assertEquals("txn-123", MDC.get("otel.transaction.id"));
        assertEquals("john.doe", MDC.get("otel.tracker.user"));

        // Verify other keys are not not removed
        assertEquals("should-be-preserved", MDC.get("other.key"));
    }

    @Test
    void testRestoreKeys() {
        OtelContextExtension extension = new OtelContextExtension();

        // Prepare keys to restore
        Map<String, String> keysToRestore = new HashMap<>();
        keysToRestore.put("otel.transaction.id", "txn-456");
        keysToRestore.put("otel.tracker.session", "sess-789");

        // Clear MDC first
        MDC.clear();

        // Restore keys
        extension.restoreKeys(keysToRestore);

        // Verify keys are restored to MDC
        assertEquals("txn-456", MDC.get("otel.transaction.id"));
        assertEquals("sess-789", MDC.get("otel.tracker.session"));
    }

    @Test
    void testEmptyKeysRestore() {
        OtelContextExtension extension = new OtelContextExtension();

        // Should not throw with empty map
        assertDoesNotThrow(() -> {
            extension.restoreKeys(new HashMap<>());
        });
    }
}
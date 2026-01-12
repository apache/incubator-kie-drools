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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoryLeakPreventionTest {

    @Test
    void shouldClearProcessContextsManually() {
        OtelContextHolder.setProcessStartContext("process-1", "transaction-1");
        OtelContextHolder.setProcessCompletionContext("process-1", 1000L, "COMPLETED");

        assertEquals("transaction-1", OtelContextHolder.getProcessStartContext("process-1"));
        assertNotNull(OtelContextHolder.getProcessCompletionContext("process-1"));

        OtelContextHolder.clearProcessContexts("process-1");

        assertNull(OtelContextHolder.getProcessStartContext("process-1"));
        assertNull(OtelContextHolder.getProcessCompletionContext("process-1"));
    }

    @Test
    void shouldEnforceMaxSizeLimit() {
        for (int i = 0; i < 200; i++) {
            OtelContextHolder.setProcessStartContext("process-" + i, "transaction-" + i);
        }

        OtelContextHolder.enforceMaxSize();

        int remainingEntries = 0;
        for (int i = 0; i < 200; i++) {
            if (OtelContextHolder.getProcessStartContext("process-" + i) != null) {
                remainingEntries++;
            }
        }

        assertTrue(remainingEntries <= 100, "Should have at most 100 entries, but had " + remainingEntries);
    }
}

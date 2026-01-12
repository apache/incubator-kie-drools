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

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeaderContextExtractorTest {

    private HeaderContextExtractor extractor;

    @BeforeEach
    public void setUp() {
        extractor = new HeaderContextExtractor();
    }

    @Test
    public void shouldCreateHeaderContextExtractor() {
        assertNotNull(extractor);
    }

    @Test
    public void shouldExtractTransactionId() {
        Map<String, String> headers = Map.of("X-TRANSACTION-ID", "txn-12345");

        Map<String, String> result = extractor.extractContextFromHeaders(headers);

        assertEquals("txn-12345", result.get("transaction.id"));
    }

    @Test
    public void shouldExtractTrackerHeaders() {
        Map<String, String> headers = Map.of(
                "X-TRACKER-USER", "john.doe",
                "X-TRACKER-SESSION", "sess-789");

        Map<String, String> result = extractor.extractContextFromHeaders(headers);

        assertEquals("john.doe", result.get("tracker.user"));
        assertEquals("sess-789", result.get("tracker.session"));
    }

    @Test
    public void shouldExtractMixedHeaders() {
        Map<String, String> headers = Map.of(
                "X-TRANSACTION-ID", "txn-12345",
                "X-TRACKER-USER", "john.doe",
                "X-TRACKER-REQUEST-ID", "req-456",
                "Content-Type", "application/json");

        Map<String, String> result = extractor.extractContextFromHeaders(headers);

        assertEquals("txn-12345", result.get("transaction.id"));
        assertEquals("john.doe", result.get("tracker.user"));
        assertEquals("req-456", result.get("tracker.request.id"));
        assertEquals(3, result.size());
    }

    @Test
    public void shouldReturnEmptyMapWhenNoRelevantHeaders() {
        Map<String, String> headers = Map.of(
                "Content-Type", "application/json",
                "Authorization", "Bearer token");

        Map<String, String> result = extractor.extractContextFromHeaders(headers);

        assertTrue(result.isEmpty());
    }
}
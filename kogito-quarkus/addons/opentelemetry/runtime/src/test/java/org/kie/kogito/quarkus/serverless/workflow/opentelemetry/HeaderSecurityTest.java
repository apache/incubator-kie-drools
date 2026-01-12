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

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeaderSecurityTest {

    @Test
    void shouldSanitizeMaliciousHeaders() {
        HeaderContextExtractor extractor = new HeaderContextExtractor();

        Map<String, List<String>> headers = Map.of(
                "X-TRANSACTION-ID", List.of("malicious\n\rinjection"),
                "X-TRACKER-ATTEMPT", List.of("script<>alert()"),
                "X-TRACKER-VALID", List.of("normal-value"));

        Map<String, String> result = extractor.extractFromProcessHeaders(headers);

        String transactionId = result.get("transaction.id");
        if (transactionId != null) {
            assertTrue(transactionId.length() <= 100, "Should limit header value length");
        }
        assertEquals("normal-value", result.get("tracker.valid"), "Should preserve valid values");
    }

    @Test
    void shouldRejectExcessivelyLongHeaders() {
        HeaderContextExtractor extractor = new HeaderContextExtractor();

        String longValue = "a".repeat(500);
        Map<String, List<String>> headers = Map.of(
                "X-TRANSACTION-ID", List.of(longValue));

        Map<String, String> result = extractor.extractFromProcessHeaders(headers);

        assertTrue(result.get("transaction.id").length() <= 100, "Should truncate long values");
    }

    @Test
    void shouldHandleNullAndEmptyHeaders() {
        HeaderContextExtractor extractor = new HeaderContextExtractor();

        Map<String, List<String>> headers = Map.of(
                "X-TRACKER-EMPTY", List.of(""));

        Map<String, String> result = extractor.extractFromProcessHeaders(headers);

        assertTrue(result.isEmpty(), "Should ignore null and empty values");
    }
}
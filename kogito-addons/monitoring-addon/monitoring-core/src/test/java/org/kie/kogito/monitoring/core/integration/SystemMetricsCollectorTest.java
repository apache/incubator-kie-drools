/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.monitoring.core.integration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.core.MonitoringRegistry;
import org.kie.kogito.monitoring.core.system.metrics.SystemMetricsCollector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SystemMetricsCollectorTest {

    private static final String handler = "hello";
    MeterRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new SimpleMeterRegistry();
        SystemMetricsCollector.setRegistry(registry);
    }

    @AfterEach
    public void cleanUp(){
        SystemMetricsCollector.setRegistry(MonitoringRegistry.getDefaultMeterRegistry());
    }

    @Test
    public void givenAStatusCodeWhenRegisterStatusCodeRequestIsCalledThenTheStatusCodeIsExportedToPrometheus() {
        // Arrange
        HashMap<String, Integer> mapCodeRepetition = new HashMap<>();
        mapCodeRepetition.put("400", 100);
        mapCodeRepetition.put("404", 50);

        // Act
        IntStream.range(0, mapCodeRepetition.get("400")).forEach(x -> SystemMetricsCollector.registerStatusCodeRequest(handler, "400"));
        IntStream.range(0, mapCodeRepetition.get("404")).forEach(x -> SystemMetricsCollector.registerStatusCodeRequest(handler, "404"));

        // Assert
        assertEquals(mapCodeRepetition.get("400"), (int)registry.find("api_http_response_code").tag("identifier", "400").counter().count());
        assertEquals(mapCodeRepetition.get("404"), (int)registry.find("api_http_response_code").tag("identifier", "404").counter().count());
    }

    @Test
    public void givenAnExceptionWhenRegisterExceptionIsCalledThenTheStatusCodeIsExportedToPrometheus() {
        // Arrange
        HashMap<String, Integer> mapExceptionRepetition = new HashMap<>();
        mapExceptionRepetition.put("NoSuchElement", 10);
        mapExceptionRepetition.put("Exception", 20);

        // Act
        IntStream.range(0, mapExceptionRepetition.get("NoSuchElement")).forEach(x -> SystemMetricsCollector.registerException(handler, "NoSuchElement"));
        IntStream.range(0, mapExceptionRepetition.get("Exception")).forEach(x -> SystemMetricsCollector.registerException(handler, "Exception"));

        // Assert
        assertEquals(mapExceptionRepetition.get("NoSuchElement"), (int)registry.find("api_http_stacktrace_exceptions")
                .tag("identifier", "NoSuchElement")
                .counter().count());
        assertEquals(mapExceptionRepetition.get("Exception"), (int)registry.find("api_http_stacktrace_exceptions")
                .tag("identifier", "Exception")
                .counter().count());
    }

    @Test
    public void givenAnElapsedTimeSampleWhenRegisterElapsedTimeSampleMetricsThenTheStatusCodeIsExportedToPrometheus() {
        // Act
        IntStream.range(1, 10001).forEach(x -> SystemMetricsCollector.registerElapsedTimeSampleMetrics(handler, x));

        // Assert
        assertTrue(registry.find("api_execution_elapsed")
                           .timer().max(TimeUnit.NANOSECONDS) >= 5000);
    }
}

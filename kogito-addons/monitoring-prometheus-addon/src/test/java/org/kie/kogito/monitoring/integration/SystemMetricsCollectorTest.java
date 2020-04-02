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

package org.kie.kogito.monitoring.integration;

import java.util.HashMap;
import java.util.stream.IntStream;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.system.metrics.SystemMetricsCollector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemMetricsCollectorTest {

    private static final String handler = "hello";
    CollectorRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = CollectorRegistry.defaultRegistry;
    }

    @Test
    public void GivenAStatusCode_WhenRegisterStatusCodeRequestIsCalled_ThenTheStatusCodeIsExportedToPrometheus(){
        // Arrange
        HashMap<String, Integer> mapCodeRepetition = new HashMap<>();
        mapCodeRepetition.put("400", 100);
        mapCodeRepetition.put("404", 50);

        // Act
        IntStream.range(0, mapCodeRepetition.get("400")).forEach(x -> SystemMetricsCollector.registerStatusCodeRequest(handler, "400"));
        IntStream.range(0, mapCodeRepetition.get("404")).forEach(x -> SystemMetricsCollector.registerStatusCodeRequest(handler, "404"));

        // Assert
        assertEquals(mapCodeRepetition.get("400"), getLabelsValue("api_http_response_code", handler, "400").intValue());
        assertEquals(mapCodeRepetition.get("404"), getLabelsValue("api_http_response_code", handler, "404").intValue());
    }

    @Test
    public void GivenAnException_WhenRegisterExceptionIsCalled_ThenTheStatusCodeIsExportedToPrometheus(){
        // Arrange
        HashMap<String, Integer> mapExceptionRepetition = new HashMap<>();
        mapExceptionRepetition.put("NoSuchElement", 10);
        mapExceptionRepetition.put("Exception", 20);

        // Act
        IntStream.range(0, mapExceptionRepetition.get("NoSuchElement")).forEach(x -> SystemMetricsCollector.registerException(handler, "NoSuchElement"));
        IntStream.range(0, mapExceptionRepetition.get("Exception")).forEach(x -> SystemMetricsCollector.registerException(handler, "Exception"));

        // Assert
        assertEquals(mapExceptionRepetition.get("NoSuchElement"), getLabelsValue("api_http_stacktrace_exceptions", handler, "NoSuchElement").intValue());
        assertEquals(mapExceptionRepetition.get("Exception"), getLabelsValue("api_http_stacktrace_exceptions", handler, "Exception").intValue());
    }

    @Test
    public void GivenAnElapsedTimeSample_WhenRegisterElapsedTimeSampleMetrics_ThenTheStatusCodeIsExportedToPrometheus(){
        // Arrange
        HashMap<Double, Double> expectedQuantiles = new HashMap<>();
        expectedQuantiles.put(0.1, 999.0);
        expectedQuantiles.put(0.25, 2525.0 );
        expectedQuantiles.put(0.5, 5042.0);
        expectedQuantiles.put(0.75,7551.0);
        expectedQuantiles.put(0.9, 9062.0);
        expectedQuantiles.put(0.99, 10000.0);

        // Act
        IntStream.range(1, 10001).forEach(x -> SystemMetricsCollector.registerElapsedTimeSampleMetrics(handler, x));

        // Assert
        for(Double key : expectedQuantiles.keySet()){
            assertEquals(expectedQuantiles.get(key), getQuantile("api_execution_elapsed_nanosecond", handler, key), 5);
        }
    }

    private double getQuantile(String name, String labelValue, double q) {
        return registry.getSampleValue(name, new String[]{"endpoint", "quantile"}, new String[]{labelValue, Collector.doubleToGoString(q)}).doubleValue();
    }

    private Double getLabelsValue(String name, String handler, String identifier) {
        return registry.getSampleValue(name, new String[]{"endpoint", "identifier"}, new String[]{handler, identifier});
    }
}

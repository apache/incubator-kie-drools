/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class TestMeterRegistry extends SimpleMeterRegistry {
    final Map<String, Map<String, BigDecimal>> measurementMap;

    public TestMeterRegistry() {
        super(SimpleConfig.DEFAULT, new MockClock());
        measurementMap = new HashMap<>();
    }

    public MockClock getClock() {
        return MockClock.clock(this);
    }

    public BigDecimal getMeasurement(String key, String statistic) {
        if (measurementMap.containsKey(key)) {
            Map<String, BigDecimal> meterMeasurementMap = measurementMap.get(key);
            if (meterMeasurementMap.containsKey(statistic)) {
                return meterMeasurementMap.get(statistic);
            } else {
                throw new IllegalArgumentException(
                        "Meter (" + key + ") does not have statistic (" + statistic + "). Available statistics are: "
                                + meterMeasurementMap.keySet().stream().collect(Collectors.joining(", ", "[", "]")));
            }
        } else {
            throw new IllegalArgumentException("Meter (" + key + ") does not exist. Available statistics are: "
                    + measurementMap.keySet().stream().collect(Collectors.joining(", ", "[", "]")));
        }
    }

    public void publish() {
        getMeters().forEach(meter -> {
            final Map<String, BigDecimal> meterMeasurementMap = new HashMap<>();
            measurementMap.put(meter.getId().getConventionName(NamingConvention.dot), meterMeasurementMap);
            meter.measure().forEach(measurement -> {
                meterMeasurementMap.put(measurement.getStatistic().name(), BigDecimal.valueOf(measurement.getValue()));
            });
        });
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.SECONDS;
    }
}

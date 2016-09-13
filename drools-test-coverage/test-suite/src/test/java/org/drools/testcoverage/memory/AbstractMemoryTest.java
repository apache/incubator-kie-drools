/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.memory;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.TurtleTestCategory;
import org.drools.testcoverage.common.util.TimeUtil;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(TurtleTestCategory.class)
public abstract class AbstractMemoryTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected void measureMemoryFootprintInTime(final long numberOfIterations, final int numberOfAveragedIterations,
            final int acceptedNumberOfMemoryRaises, final long waitEachIterationMillis) {
        final Runtime runtime = Runtime.getRuntime();

        int memoryRaiseCount = 0;

        long averageMemory = 0;
        final List<Long> averageMemoryFootprints = new ArrayList<Long>();

        for (long i = 1; i < numberOfIterations; i++) {
            TimeUtil.waitBusy(waitEachIterationMillis);

            final long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            averageMemory = averageMemory + usedMemory;
            if ((i % numberOfAveragedIterations) == 0) {
                averageMemory = averageMemory / numberOfAveragedIterations;
                if (averageMemoryFootprints.size() > 0) {
                    final long previousAverageMemory = averageMemoryFootprints.get(averageMemoryFootprints.size() - 1);
                    if (averageMemory > previousAverageMemory) {
                        memoryRaiseCount++;
                    } else {
                        memoryRaiseCount = 0;
                    }
                    assertFalse(
                            "Memory raised during " + (acceptedNumberOfMemoryRaises + 1)
                                    + " consecutive measurements, there is probably some memory leak! "
                                    + getMemoryMeasurementsString(averageMemoryFootprints),
                            memoryRaiseCount > acceptedNumberOfMemoryRaises);
                }
                logger.info("Average memory: " + averageMemory);
                averageMemoryFootprints.add(averageMemory);
                averageMemory = 0;
            }
        }
    }

    private String getMemoryMeasurementsString(final List<Long> memoryMeasurements) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Measured used memory: ");
        for (int i = 1; i <= memoryMeasurements.size(); i++) {
            final Long measurement = memoryMeasurements.get(i - 1) / 1024 / 1024;
            builder.append(i + ": " + measurement + " MB; ");
        }
        return builder.toString();
    }
}

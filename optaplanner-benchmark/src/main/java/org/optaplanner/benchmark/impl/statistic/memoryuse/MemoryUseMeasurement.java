/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.statistic.memoryuse;

public class MemoryUseMeasurement {

    public static MemoryUseMeasurement create() {
        Runtime runtime = Runtime.getRuntime();
        return new MemoryUseMeasurement(runtime.totalMemory() - runtime.freeMemory(), runtime.maxMemory());
    }

    private final long usedMemory;
    private final long maxMemory;

    public MemoryUseMeasurement(long usedMemory, long maxMemory) {
        this.usedMemory = usedMemory;
        this.maxMemory = maxMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

}

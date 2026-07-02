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

package org.optaplanner.benchmark.impl.statistic.memoryuse;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class MemoryUseStatisticPoint extends StatisticPoint {

    public static MemoryUseStatisticPoint create(long timeMillisSpent) {
        Runtime runtime = Runtime.getRuntime();
        return new MemoryUseStatisticPoint(timeMillisSpent, runtime.totalMemory() - runtime.freeMemory(), runtime.maxMemory());
    }

    private final long timeMillisSpent;
    private final long usedMemory;
    private final long maxMemory;

    public MemoryUseStatisticPoint(long timeMillisSpent, long usedMemory, long maxMemory) {
        this.timeMillisSpent = timeMillisSpent;
        this.usedMemory = usedMemory;
        this.maxMemory = maxMemory;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, usedMemory, maxMemory);
    }

}

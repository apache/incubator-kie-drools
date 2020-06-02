/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.util;

import org.drools.core.common.BaseNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerfLogUtils {

    private static final Logger logger = LoggerFactory.getLogger(PerfLogUtils.class);

    public static final String PERF_LOGGER_ENABLED = "drools.performance.logger.enabled";
    private static boolean enabled = Boolean.parseBoolean(System.getProperty(PERF_LOGGER_ENABLED, "false"));

    public static final String PERF_LOGGER_THRESHOLD = "drools.performance.logger.threshold";
    private static int threshold = Integer.parseInt(System.getProperty(PERF_LOGGER_THRESHOLD, "500")); // microseconds

    private static final ThreadLocal<NodeStats> nodeStats = new ThreadLocal<>();

    public static int getThreshold() {
        return threshold;
    }

    public static void setThreshold(int threshold) {
        PerfLogUtils.threshold = threshold;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        PerfLogUtils.enabled = enabled;
    }

    public static void startMetrics(BaseNode baseNode) {
        if (enabled) {
            nodeStats.set(new NodeStats(baseNode));
        }
    }

    public static void incrementEvalCount() {
        if (enabled) {
            NodeStats stats = nodeStats.get();
            if (stats != null && stats.isStarted()) {
                stats.incrementEvalCount();
            } // don't log warn for stats == null because an evaluation may be executed under metrics or not.
        }
    }

    public static void logAndEndMetrics() {
        if (enabled) {
            NodeStats stats = nodeStats.get();
            if (stats != null && stats.isStarted()) {
                long elapsedTimeInMicro = (System.nanoTime() - stats.getStartTime()) / 1000;
                if (stats.getEvalCount() > 0 && elapsedTimeInMicro > threshold) {
                    logger.trace("{}, evalCount:{}, elapsedMicro:{}", stats.getNode(), stats.getEvalCount(), elapsedTimeInMicro);
                }
            } else {
                logger.warn("nodeStats has to be initialized. Call startMetrics() beforehand : stats = {}", stats);
            }
            nodeStats.remove();
        }
    }

    private PerfLogUtils() {
        // It is not allowed to create instances of util classes.
    }
}

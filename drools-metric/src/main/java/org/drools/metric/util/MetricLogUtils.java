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

package org.drools.metric.util;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.Search;
import org.drools.core.common.BaseNode;
import org.kie.api.definition.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricLogUtils {

    private static final Logger logger = LoggerFactory.getLogger(MetricLogUtils.class);

    public static final String METRIC_LOGGER_ENABLED = "drools.metric.logger.enabled";
    private boolean enabled = Boolean.parseBoolean(System.getProperty(METRIC_LOGGER_ENABLED, "false"));
    private MeterRegistry meterRegistry = getMicrometerRegistryIfEnabled();

    public static final String METRIC_LOGGER_THRESHOLD = "drools.metric.logger.threshold";
    private int threshold = Integer.parseInt(System.getProperty(METRIC_LOGGER_THRESHOLD, "500")); // microseconds

    private final ThreadLocal<NodeStats> nodeStats = new ThreadLocal<>();

    private static final MetricLogUtils INSTANCE = new MetricLogUtils();

    private static MeterRegistry getMicrometerRegistryIfEnabled() {
        try {
            Class.forName("io.micrometer.core.instrument.Tag");
            return Metrics.globalRegistry;
        } catch (Exception e) {
            return null;
        }
    }

    public static MetricLogUtils getInstance() {
        return MetricLogUtils.INSTANCE;
    }

    private MetricLogUtils() {
        // It is not allowed to create instances of util classes.
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void startMetrics(BaseNode baseNode) {
        if (enabled) {
            nodeStats.set(new NodeStats(baseNode));
        } else {
            logger.warn("Metrics must not be started when disabled");
        }
    }

    public void incrementEvalCount() {
        if (enabled) {
            NodeStats stats = nodeStats.get();
            if (stats != null && stats.isStarted()) {
                stats.incrementEvalCount();
            }
        } else {
            logger.warn("Metrics must not be executed when disabled");
        }
    }

    public void logAndEndMetrics() {
        if (enabled) {
            NodeStats stats = nodeStats.get();
            if (stats != null && stats.isStarted()) {
                long evalCount = stats.getEvalCount();
                long elapsedTimeInNanos = (System.nanoTime() - stats.getStartTime());
                long elapsedTimeInMicro = elapsedTimeInNanos / 1000;
                if (evalCount > 0 && elapsedTimeInMicro > threshold) {
                    if (meterRegistry == null) { // Only log when Micrometer is not enabled.
                        logger.trace("{}, evalCount:{}, elapsedMicro:{}", stats.getNode(), evalCount, elapsedTimeInMicro);
                    } else {
                        triggerMicrometerTimer(stats.getNode(), evalCount, elapsedTimeInNanos);
                    }
                }
            } else {
                logger.warn("nodeStats has to be initialized. Call startMetrics() beforehand : stats = {}", stats);
            }
            nodeStats.remove();
        }
    }

    private void triggerMicrometerTimer(BaseNode node, long evalCount, long elapsedTimeInNanos) {
        // TODO This takes a long time; cache this somehow.
        Tag nodeIdTag = Tag.of("node.id", Long.toString(node.getId()));
        Stream<Tag> allTags = Stream.of(nodeIdTag);
        for (Rule rule : node.getAssociatedRules()) {
            String ruleName = rule.getPackageName() + "." + rule.getName(); // TODO sanitize rule names (whitespace etc.)
            Tag ruleTag = Tag.of("rule", ruleName);
            allTags = Stream.concat(allTags, Stream.of(ruleTag));
        }
        Iterable<Tag> tagsIterable = allTags.collect(Collectors.toSet()); // TODO Somehow identify the session/kiebase too.
        // Look up the timer in the registry.
        // TODO This takes a long time; cache this somehow.
        Timer registeredTimer = Search.in(meterRegistry)
                .tags(tagsIterable)
                .timer();
        if (registeredTimer == null) { // If timer does not exist, create one.
            registeredTimer = Timer.builder("org.drools.metric.time.elapsed")
                    .tags(tagsIterable)
                    .register(meterRegistry);
        }
        // Now record the average elapsed time.
        registeredTimer.record(Duration.ofNanos(elapsedTimeInNanos / evalCount));
    }

}

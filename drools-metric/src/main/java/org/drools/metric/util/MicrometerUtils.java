package org.drools.metric.util;

import java.time.Duration;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import org.drools.core.common.BaseNode;
import org.kie.api.definition.rule.Rule;

/**
 * All references to Micrometer are in this class, and therefore Micrometer is only required on the classpath when
 * this class is actually loaded.
 */
public final class MicrometerUtils {

    public static final MicrometerUtils INSTANCE = new MicrometerUtils();

    private final Map<BaseNode, Timer> averageElapsedTimeCache = new WeakHashMap<>(0);
    private final Map<BaseNode, Timer> elapsedTimeCache = new WeakHashMap<>(0);
    private final Map<BaseNode, Counter> evaluationCountCache = new WeakHashMap<>(0);

    private MicrometerUtils() {
        // No external instances.
    }

    public void triggerMicrometer(BaseNode node, long evalCount, long elapsedTimeInNanos) {
        MeterRegistry meterRegistry = Metrics.globalRegistry;
        MicrometerUtils.triggerMicrometer(averageElapsedTimeCache,
                tags -> Timer.builder("org.drools.metric.elapsed.time.per.evaluation")
                        .tags(tags)
                        .register(meterRegistry),
                timer -> timer.record(Duration.ofNanos(elapsedTimeInNanos / evalCount)),
                node);
        MicrometerUtils.triggerMicrometer(elapsedTimeCache,
                tags -> Timer.builder("org.drools.metric.elapsed.time")
                        .tags(tags)
                        .register(meterRegistry),
                timer -> timer.record(Duration.ofNanos(elapsedTimeInNanos)),
                node);
        MicrometerUtils.triggerMicrometer(evaluationCountCache,
                tags -> Counter.builder("org.drools.metric.evaluation.count")
                        .tags(tags)
                        .register(meterRegistry),
                counter -> counter.increment(evalCount),
                node);
    }

    private static <Meter_ extends Meter> void triggerMicrometer(Map<BaseNode, Meter_> cache, Function<Iterable<Tag>,
            Meter_> meterConstructor, Consumer<Meter_> meterRecorder, BaseNode node) {
        Meter_ meter = cache.computeIfAbsent(node, k -> { // Meter lookups take a lot of time; we cache meters per node.
            Tag nodeIdTag = Tag.of("node.id", Long.toString(node.getId()));
            Stream<Tag> allTags = Stream.of(nodeIdTag);
            for (Rule rule : node.getAssociatedRules()) {
                String ruleName = rule.getPackageName() + "." + rule.getName();
                Tag ruleTag = Tag.of("rule", ruleName);
                allTags = Stream.concat(allTags, Stream.of(ruleTag));
            }
            Iterable<Tag> tagsIterable = allTags.collect(Collectors.toSet());
            // Look up the timer in the registry.
            return meterConstructor.apply(tagsIterable);
        });
        // Now record the average elapsed time.
        meterRecorder.accept(meter);
    }

    public void clear() { // For testing.
        averageElapsedTimeCache.clear();
        elapsedTimeCache.clear();
        evaluationCountCache.clear();
    }

}

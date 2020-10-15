/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.tracing.decision.aggregator.Aggregator;
import org.kie.kogito.tracing.decision.aggregator.DefaultAggregator;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.terminationdetector.CounterTerminationDetector;
import org.kie.kogito.tracing.decision.terminationdetector.TerminationDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTracingCollector {

    private static final Logger LOG = LoggerFactory.getLogger(DecisionTracingCollector.class);

    private final Map<String, List<EvaluateEvent>> cacheMap;
    private final Map<String, TerminationDetector> terminationDetectorMap;
    private final Aggregator aggregator;
    private final Consumer<String> payloadConsumer;
    private final BiFunction<String, String, DMNModel> modelSupplier;
    private final Supplier<TerminationDetector> terminationDetectorSupplier;
    private final ConfigBean configBean;

    public DecisionTracingCollector(Consumer<String> payloadConsumer, BiFunction<String, String, DMNModel> modelSupplier, ConfigBean configBean) {
        this(new DefaultAggregator(), payloadConsumer, modelSupplier, CounterTerminationDetector::new, configBean);
    }

    public DecisionTracingCollector(
            Aggregator aggregator,
            Consumer<String> payloadConsumer,
            BiFunction<String, String, DMNModel> modelSupplier,
            Supplier<TerminationDetector> terminationDetectorSupplier,
            ConfigBean configBean
    ) {
        this.cacheMap = new HashMap<>();
        this.terminationDetectorMap = new HashMap<>();
        this.aggregator = aggregator;
        this.payloadConsumer = payloadConsumer;
        this.modelSupplier = modelSupplier;
        this.terminationDetectorSupplier = terminationDetectorSupplier;
        this.configBean = configBean;
    }

    public void addEvent(EvaluateEvent event) {
        LOG.trace("Received {}(executionId: {}, modelName: {}, modelNamespace: {})", event.getType(), event.getExecutionId(), event.getModelName(), event.getModelNamespace());

        String executionId = event.getExecutionId();
        if (!cacheMap.containsKey(executionId)) {
            cacheMap.put(executionId, new LinkedList<>());
            terminationDetectorMap.put(executionId, terminationDetectorSupplier.get());
            LOG.trace("Added evaluation {} to cache (current size: {})", executionId, cacheMap.size());
        }

        cacheMap.get(executionId).add(event);
        terminationDetectorMap.get(executionId).add(event);

        if (terminationDetectorMap.get(executionId).isTerminated()) {
            DMNModel dmnModel = modelSupplier.apply(event.getModelNamespace(), event.getModelName());
            Optional<String> optPayload = aggregate(dmnModel, executionId, cacheMap.get(executionId));

            if (optPayload.isPresent()) {
                String payload = optPayload.get();
                payloadConsumer.accept(payload);
                LOG.debug("Generated aggregated event for evaluation {} (length {})", executionId, payload.length());
            } else {
                LOG.error("Failed aggregating data for evaluation {}", executionId);
            }

            cacheMap.remove(executionId);
            terminationDetectorMap.remove(executionId);
            LOG.trace("Removed evaluation {} from cache (current size: {})", executionId, cacheMap.size());
        }
    }

    private Optional<String> aggregate(DMNModel model, String executionId, List<EvaluateEvent> events) {
        return aggregator.aggregate(model, executionId, events, configBean).flatMap(CloudEventUtils::encode);
    }
}

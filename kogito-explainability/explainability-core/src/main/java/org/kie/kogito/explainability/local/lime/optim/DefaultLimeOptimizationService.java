/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.lime.optim;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for {@link LimeOptimizationService}.
 */
public class DefaultLimeOptimizationService implements LimeOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLimeOptimizationService.class);

    private final Deque<CompletableFuture<Void>> queue;
    private final LinkedHashMap<LimeExplainer, LimeConfig> register;
    private final LimeConfigOptimizer limeConfigOptimizer;
    private final int maxJobsExecuted;

    public DefaultLimeOptimizationService(LimeConfigOptimizer limeConfigOptimizer, int maxJobsExecuted) {
        this.maxJobsExecuted = maxJobsExecuted;
        this.limeConfigOptimizer = limeConfigOptimizer;
        this.register = new LinkedHashMap<LimeExplainer, LimeConfig>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<LimeExplainer, LimeConfig> eldest) {
                return size() > Math.max(10, maxJobsExecuted);
            }
        };
        this.queue = new ConcurrentLinkedDeque<>();
    }

    @Override
    public boolean submit(LimeOptimizationRequest limeOptimizationRequest) {
        if (queue.size() < maxJobsExecuted) {
            CompletableFuture<Void> completableFuture = CompletableFuture
                    .supplyAsync(() -> limeConfigOptimizer.optimize(limeOptimizationRequest.getLimeConfig(), limeOptimizationRequest.getPredictions(),
                            limeOptimizationRequest.getPredictionProvider()))
                    .thenAccept(c -> register.put(limeOptimizationRequest.getExplainer(), c))
                    .thenRun(flushQueue());
            logger.info("optimization job submitted");
            return queue.offer(completableFuture);
        }
        logger.warn("busy optimizing (queue size: {}), next time!", queue.size());
        return false;
    }

    private Runnable flushQueue() {
        return () -> {
            List<CompletableFuture<Void>> finished = queue.stream().filter(CompletableFuture::isDone).collect(Collectors.toList());
            for (CompletableFuture<Void> f : finished) {
                queue.remove(f);
            }
        };
    }

    @Override
    public LimeConfig getBestConfigFor(LimeExplainer limeExplainer) {
        return register.get(limeExplainer);
    }

}

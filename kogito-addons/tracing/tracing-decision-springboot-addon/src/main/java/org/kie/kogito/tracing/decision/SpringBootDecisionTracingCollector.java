/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.modelsupplier.ApplicationModelSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class SpringBootDecisionTracingCollector {

    private final DecisionTracingCollector collector;

    public SpringBootDecisionTracingCollector(final SpringBootTraceEventEmitter eventEmitter,
                                              final BiFunction<String, String, DMNModel> modelSupplier,
                                              final ConfigBean configBean) {
        this.collector = new DecisionTracingCollector(eventEmitter::emit, modelSupplier, configBean);
    }

    @Autowired
    public SpringBootDecisionTracingCollector(final Application application,
                                              final SpringBootTraceEventEmitter eventEmitter,
                                              final ConfigBean configBean) {
        this(eventEmitter, new ApplicationModelSupplier(application), configBean);
    }

    @Async("kogitoTracingDecisionAddonTaskExecutor")
    @EventListener
    public void onApplicationEvent(final EvaluateEvent event) {
        collector.addEvent(event);
    }
}

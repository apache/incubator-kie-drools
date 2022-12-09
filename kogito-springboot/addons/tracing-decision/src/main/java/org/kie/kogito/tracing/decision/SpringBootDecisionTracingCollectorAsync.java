/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.decision;

import java.util.function.BiFunction;

import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.Application;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class SpringBootDecisionTracingCollectorAsync extends SpringBootDecisionTracingCollector {

    public SpringBootDecisionTracingCollectorAsync(SpringBootTraceEventEmitter eventEmitter, ConfigBean configBean, BiFunction<String, String, DMNModel> modelSupplier) {
        super(eventEmitter, configBean, modelSupplier);
    }

    public SpringBootDecisionTracingCollectorAsync(SpringBootTraceEventEmitter eventEmitter, ConfigBean configBean, Application application) {
        super(eventEmitter, configBean, application);
    }

    @Override
    @Async("kogitoTracingDecisionAddonTaskExecutor")
    @EventListener
    public void onApplicationEvent(final EvaluateEvent event) {
        super.onApplicationEvent(event);
    }
}

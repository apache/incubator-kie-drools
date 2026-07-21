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
package org.kie.kogito.tracing.decision;

import java.util.function.BiFunction;

import org.kie.kogito.Application;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.modelsupplier.ApplicationModelSupplier;

import io.quarkus.vertx.ConsumeEvent;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class QuarkusDecisionTracingCollector {

    private final DecisionTracingCollector collector;

    public QuarkusDecisionTracingCollector(final QuarkusTraceEventEmitter eventEmitter,
            final ConfigBean configBean,
            final BiFunction<String, String, org.kie.dmn.api.core.DMNModel> modelSupplier) {
        this.collector = new DecisionTracingCollector(eventEmitter::emit, modelSupplier, configBean);
    }

    @Inject
    public QuarkusDecisionTracingCollector(final QuarkusTraceEventEmitter eventEmitter,
            final ConfigBean configBean,
            final Application application) {
        this(eventEmitter, configBean, new ApplicationModelSupplier(application));
    }

    @ConsumeEvent("kogito-tracing-decision_EvaluateEvent")
    public void onEvent(final EvaluateEvent event) {
        collector.addEvent(event);
    }
}

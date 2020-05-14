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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.eventbus.EventBus;

/**
 * This class must always have exact FQCN as <code>org.kie.kogito.tracing.decision.KogitoDecisionTracingListener</code>
 */
@ApplicationScoped
public final class KogitoDecisionTracingListener extends DecisionTracingListener {

    @Inject
    public KogitoDecisionTracingListener(EventBus bus) {
        setEventConsumer(event ->
                bus.send(String.format("kogito-tracing-decision_%s", event.getClass().getSimpleName()), event)
        );
    }

}

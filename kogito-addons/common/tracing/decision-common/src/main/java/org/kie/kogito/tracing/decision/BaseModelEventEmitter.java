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

import org.kie.kogito.decision.DecisionModelResourcesProvider;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.tracing.EventEmitter;
import org.kie.kogito.tracing.event.model.ModelEvent;
import org.kie.kogito.tracing.event.model.models.DecisionModelEvent;

public abstract class BaseModelEventEmitter implements EventEmitter {

    private final DecisionModelResourcesProvider decisionModelResourcesProvider;

    public BaseModelEventEmitter(final DecisionModelResourcesProvider decisionModelResourcesProvider) {
        this.decisionModelResourcesProvider = decisionModelResourcesProvider;
    }

    protected void publishDecisionModels() {
        decisionModelResourcesProvider.get().forEach(resource -> {
            //Fire a new ModelEvent containing the model, name and namespace
            CloudEventUtils.urlEncodedURIFrom(ModelEvent.class.getName())
                    .flatMap(uri -> CloudEventUtils.build("id",
                            uri,
                            new DecisionModelEvent(
                                    resource.getGav(),
                                    resource.getModelName(),
                                    resource.getNamespace(),
                                    resource.getModelMetadata(),
                                    resource.get()),
                            ModelEvent.class))
                    .flatMap(CloudEventUtils::encode)
                    .ifPresent(this::emit);
        });
    }
}

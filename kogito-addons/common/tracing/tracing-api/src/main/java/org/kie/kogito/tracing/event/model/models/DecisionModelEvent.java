/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.event.model.models;

import org.kie.kogito.KogitoGAV;
import org.kie.kogito.ModelDomain;
import org.kie.kogito.decision.DecisionModelMetadata;
import org.kie.kogito.tracing.event.model.ModelEvent;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class DecisionModelEvent extends ModelEvent<DecisionModelMetadata> {

    @JsonProperty("namespace")
    private String namespace;

    @JsonProperty("definition")
    private String definition;

    protected DecisionModelEvent() {

    }

    public DecisionModelEvent(final KogitoGAV gav,
            final String name,
            final String namespace,
            final DecisionModelMetadata decisionModelMetadata,
            final String definition) {
        super(gav, name, decisionModelMetadata, ModelDomain.DECISION);
        this.namespace = namespace;
        this.definition = definition;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getDefinition() {
        return definition;
    }
}

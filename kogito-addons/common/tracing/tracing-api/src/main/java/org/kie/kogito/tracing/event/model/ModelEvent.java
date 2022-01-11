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
package org.kie.kogito.tracing.event.model;

import org.kie.kogito.KogitoGAV;
import org.kie.kogito.ModelDomain;
import org.kie.kogito.event.ModelMetadata;
import org.kie.kogito.tracing.event.model.models.DecisionModelEvent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract <code>ModelEvent</code> to be extended by actual model-specific implementations
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "@type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DecisionModelEvent.class, name = "DECISION"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ModelEvent<T extends ModelMetadata> {

    @JsonProperty("gav")
    private KogitoGAV gav;

    @JsonProperty("name")
    private String name;

    @JsonProperty("modelMetadata")
    private T modelMetadata;

    @JsonProperty("@type")
    private ModelDomain modelDomain;

    protected ModelEvent() {

    }

    protected ModelEvent(final KogitoGAV gav,
            final String name,
            final T modelMetadata,
            final ModelDomain modelDomain) {
        this.gav = gav;
        this.name = name;
        this.modelMetadata = modelMetadata;
        this.modelDomain = modelDomain;
    }

    public KogitoGAV getGav() {
        return gav;
    }

    public String getName() {
        return name;
    }

    public T getModelMetadata() {
        return modelMetadata;
    }
}

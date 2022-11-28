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

package org.kie.kogito.trusty.service.common.responses;

import java.util.Collection;

import org.kie.kogito.ModelDomain;
import org.kie.kogito.trusty.service.common.responses.decision.DecisionStructuredInputsResponse;
import org.kie.kogito.trusty.service.common.responses.process.ProcessStructuredInputsResponse;
import org.kie.kogito.trusty.storage.api.model.Input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base abstract class for <b>StructuredInputsResponse</b>
 * 
 * @param <T>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "@type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DecisionStructuredInputsResponse.class, name = "DECISION"),
        @JsonSubTypes.Type(value = ProcessStructuredInputsResponse.class, name = "PROCESS"),
})
public abstract class StructuredInputsResponse<T extends Input> {

    @JsonProperty("inputs")
    private Collection<T> inputs;

    @JsonProperty("@type")
    private ModelDomain modelDomain;

    protected StructuredInputsResponse() {
    }

    protected StructuredInputsResponse(Collection<T> inputs, ModelDomain modelDomain) {
        this.inputs = inputs;
        this.modelDomain = modelDomain;
    }

    public Collection<T> getInputs() {
        return inputs;
    }
}

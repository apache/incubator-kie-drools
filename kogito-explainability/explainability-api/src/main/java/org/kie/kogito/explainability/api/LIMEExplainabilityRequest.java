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
package org.kie.kogito.explainability.api;

import java.util.Collection;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LIMEExplainabilityRequest extends BaseExplainabilityRequest {

    public static final String EXPLAINABILITY_TYPE_NAME = "LIME";

    @JsonProperty("inputs")
    @NotNull(message = "inputs object must be provided.")
    private Collection<NamedTypedValue> inputs;

    @JsonProperty("outputs")
    @NotNull(message = "outputs object must be provided.")
    private Collection<NamedTypedValue> outputs;

    private LIMEExplainabilityRequest() {
        super();
    }

    public LIMEExplainabilityRequest(@NotNull String executionId,
            @NotBlank String serviceUrl,
            @NotNull ModelIdentifier modelIdentifier,
            @NotNull Collection<NamedTypedValue> inputs,
            @NotNull Collection<NamedTypedValue> outputs) {
        super(executionId, serviceUrl, modelIdentifier);
        this.inputs = Objects.requireNonNull(inputs);
        this.outputs = Objects.requireNonNull(outputs);
    }

    public Collection<NamedTypedValue> getInputs() {
        return inputs;
    }

    public Collection<NamedTypedValue> getOutputs() {
        return outputs;
    }

}

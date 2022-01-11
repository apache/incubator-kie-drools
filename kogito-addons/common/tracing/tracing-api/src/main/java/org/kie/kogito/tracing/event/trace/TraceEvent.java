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
package org.kie.kogito.tracing.event.trace;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceEvent {

    @JsonProperty("header")
    private TraceHeader header;

    @JsonProperty("inputs")
    private List<TraceInputValue> inputs;

    @JsonProperty("outputs")
    private List<TraceOutputValue> outputs;

    @JsonProperty("executionSteps")
    private List<TraceExecutionStep> executionSteps;

    private TraceEvent() {
    }

    public TraceEvent(TraceHeader header, List<TraceInputValue> inputs, List<TraceOutputValue> outputs, List<TraceExecutionStep> executionSteps) {
        this.header = header;
        this.inputs = inputs;
        this.outputs = outputs;
        this.executionSteps = executionSteps;
    }

    public TraceHeader getHeader() {
        return header;
    }

    public List<TraceInputValue> getInputs() {
        return inputs;
    }

    public List<TraceOutputValue> getOutputs() {
        return outputs;
    }

    public List<TraceExecutionStep> getExecutionSteps() {
        return executionSteps;
    }
}

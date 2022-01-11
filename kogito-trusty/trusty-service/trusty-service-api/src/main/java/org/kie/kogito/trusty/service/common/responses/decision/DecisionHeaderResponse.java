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

package org.kie.kogito.trusty.service.common.responses.decision;

import java.time.OffsetDateTime;

import org.kie.kogito.ModelDomain;
import org.kie.kogito.trusty.service.common.responses.ExecutionHeaderResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The <b>Decision</b> implementation of <code>ExecutionHeaderResponse</code>.
 */
public final class DecisionHeaderResponse extends ExecutionHeaderResponse {

    @JsonProperty("executedModelNamespace")
    private String executedModelNamespace;

    protected DecisionHeaderResponse() {
        // serialization
    }

    public DecisionHeaderResponse(String executionId,
            OffsetDateTime executionDate,
            Boolean hasSucceeded,
            String executorName,
            String executedModelName,
            String executedModelNamespace) {
        super(executionId,
                executionDate,
                hasSucceeded,
                executorName,
                executedModelName,
                ModelDomain.DECISION);
        this.executedModelNamespace = executedModelNamespace;
    }

    /**
     * Gets the namespace of the executed model.
     * 
     * @return The namespace of the executed model.
     */
    public String getExecutedModelNamespace() {
        return executedModelNamespace;
    }
}

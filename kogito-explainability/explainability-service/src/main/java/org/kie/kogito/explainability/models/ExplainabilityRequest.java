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

package org.kie.kogito.explainability.models;

import org.kie.kogito.explainability.api.ExplainabilityRequestDto;

public class ExplainabilityRequest {

    private String executionId;

    public ExplainabilityRequest(String executionId) {
        this.executionId = executionId;
    }

    public static ExplainabilityRequest from(ExplainabilityRequestDto explainabilityRequestDto) {
        // TODO: Update the converter with all the properties in ExplainabilityRequestDto when they will be defined. https://issues.redhat.com/browse/KOGITO-2944
        return new ExplainabilityRequest(explainabilityRequestDto.getExecutionId());
    }

    public String getExecutionId() {
        return this.executionId;
    }
}

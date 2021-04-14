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
package org.kie.kogito.explainability.api;

public class CounterfactualExplainabilityResultDto extends BaseExplainabilityResultDto {

    public static final String EXPLAINABILITY_TYPE_NAME = "Counterfactual";

    private CounterfactualExplainabilityResultDto() {
        super();
    }

    private CounterfactualExplainabilityResultDto(String executionId, ExplainabilityStatus status, String statusDetails) {
        super(executionId, status, statusDetails);
    }

    public static CounterfactualExplainabilityResultDto buildSucceeded(String executionId) {
        return new CounterfactualExplainabilityResultDto(executionId, ExplainabilityStatus.SUCCEEDED, null);
    }

    public static CounterfactualExplainabilityResultDto buildFailed(String executionId, String statusDetails) {
        return new CounterfactualExplainabilityResultDto(executionId, ExplainabilityStatus.FAILED, statusDetails);
    }
}

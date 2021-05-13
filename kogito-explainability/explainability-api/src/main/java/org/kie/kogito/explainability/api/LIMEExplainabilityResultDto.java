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
package org.kie.kogito.explainability.api;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LIMEExplainabilityResultDto extends BaseExplainabilityResultDto {

    public static final String EXPLAINABILITY_TYPE_NAME = "lime";

    @JsonProperty("saliency")
    @NotNull(message = "saliencies object must be provided.")
    private Map<String, SaliencyDto> saliencies;

    private LIMEExplainabilityResultDto() {
        super();
    }

    private LIMEExplainabilityResultDto(@NotNull String executionId,
            @NotNull ExplainabilityStatus status,
            String statusDetails,
            @NotNull Map<String, SaliencyDto> saliencies) {
        super(executionId, status, statusDetails);
        this.saliencies = Objects.requireNonNull(saliencies);
    }

    public Map<String, SaliencyDto> getSaliencies() {
        return saliencies;
    }

    public static LIMEExplainabilityResultDto buildSucceeded(String executionId, Map<String, SaliencyDto> saliencies) {
        return new LIMEExplainabilityResultDto(executionId, ExplainabilityStatus.SUCCEEDED, null, saliencies);
    }

    public static LIMEExplainabilityResultDto buildFailed(String executionId, String statusDetails) {
        return new LIMEExplainabilityResultDto(executionId, ExplainabilityStatus.FAILED, statusDetails, Collections.emptyMap());
    }
}

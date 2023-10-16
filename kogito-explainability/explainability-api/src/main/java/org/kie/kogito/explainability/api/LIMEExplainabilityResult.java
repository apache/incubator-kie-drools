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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LIMEExplainabilityResult extends BaseExplainabilityResult {

    public static final String EXPLAINABILITY_TYPE_NAME = "lime";

    public static final String SALIENCIES_FIELD = "saliencies";

    @JsonProperty(SALIENCIES_FIELD)
    @NotNull(message = "saliencies object must be provided.")
    private List<SaliencyModel> saliencies;

    public LIMEExplainabilityResult() {
    }

    public LIMEExplainabilityResult(@NotNull String executionId,
            @NotNull ExplainabilityStatus status,
            String statusDetails,
            @NotNull List<SaliencyModel> saliencies) {
        super(executionId, status, statusDetails);
        this.saliencies = Objects.requireNonNull(saliencies);
    }

    public static LIMEExplainabilityResult buildSucceeded(String executionId, List<SaliencyModel> saliencies) {
        return new LIMEExplainabilityResult(executionId, ExplainabilityStatus.SUCCEEDED, null, saliencies);
    }

    public static LIMEExplainabilityResult buildFailed(String executionId, String statusDetails) {
        return new LIMEExplainabilityResult(executionId, ExplainabilityStatus.FAILED, statusDetails, Collections.emptyList());
    }

    public List<SaliencyModel> getSaliencies() {
        return saliencies;
    }

}

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

import java.util.List;

import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalienciesResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("statusDetails")
    @JsonInclude(NON_NULL)
    private String statusDetails;

    @JsonProperty("saliencies")
    @JsonInclude(NON_NULL)
    private List<SaliencyModel> saliencies;

    private SalienciesResponse() {
    }

    public SalienciesResponse(ExplainabilityResult explainabilityResult) {
        this(explainabilityResult.getStatus().name(), explainabilityResult.getStatusDetails(), explainabilityResult.getSaliencies());
    }

    public SalienciesResponse(String status, String statusDetails, List<SaliencyModel> saliencies) {
        this.status = status;
        this.statusDetails = statusDetails;
        this.saliencies = saliencies;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDetails() {
        return statusDetails;
    }

    public List<SaliencyModel> getSaliencies() {
        return saliencies;
    }
}

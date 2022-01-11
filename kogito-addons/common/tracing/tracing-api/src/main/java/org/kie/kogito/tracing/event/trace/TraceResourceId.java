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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceResourceId {

    @JsonProperty("serviceUrl")
    private String serviceUrl;

    @JsonProperty("modelNamespace")
    private String modelNamespace;

    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("decisionServiceId")
    @JsonInclude(NON_NULL)
    private String decisionServiceId;

    @JsonProperty("decisionServiceName")
    @JsonInclude(NON_NULL)
    private String decisionServiceName;

    private TraceResourceId() {
    }

    public TraceResourceId(String serviceUrl, String modelNamespace, String modelName) {
        this(serviceUrl, modelNamespace, modelName, null, null);
    }

    public TraceResourceId(String serviceUrl, String modelNamespace, String modelName, String decisionServiceId, String decisionServiceName) {
        this.serviceUrl = serviceUrl;
        this.modelNamespace = modelNamespace;
        this.modelName = modelName;
        this.decisionServiceId = decisionServiceId;
        this.decisionServiceName = decisionServiceName;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getModelNamespace() {
        return modelNamespace;
    }

    public String getModelName() {
        return modelName;
    }

    public String getDecisionServiceId() {
        return decisionServiceId;
    }

    public String getDecisionServiceName() {
        return decisionServiceName;
    }
}

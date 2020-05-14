/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision.event;

import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;

public abstract class EvaluateEvent {

    private final String executionId;
    private final String modelName;
    private final String modelNamespace;
    private final Map<String, Object> context;
    private final Map<String, Object> contextMetadata;
    private final EvaluateEventResult result;

    public EvaluateEvent(String executionId, String modelName, String modelNamespace, DMNContext context) {
        DMNContext clone = context.clone();
        this.executionId = executionId;
        this.modelName = modelName;
        this.modelNamespace = modelNamespace;
        this.context = clone.getAll();
        this.contextMetadata = clone.getMetadata().asMap();
        this.result = null;
    }

    public EvaluateEvent(String executionId, String modelName, String modelNamespace, DMNResult result) {
        DMNContext clone = result.getContext().clone();
        this.executionId = executionId;
        this.modelName = modelName;
        this.modelNamespace = modelNamespace;
        this.context = clone.getAll();
        this.contextMetadata = clone.getMetadata().asMap();
        this.result = EvaluateEventResult.from(result);
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelNamespace() {
        return modelNamespace;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public Map<String, Object> getContextMetadata() {
        return contextMetadata;
    }

    public EvaluateEventResult getResult() {
        return result;
    }

}

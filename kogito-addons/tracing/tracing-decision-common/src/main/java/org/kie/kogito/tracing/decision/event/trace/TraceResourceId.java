/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision.event.trace;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType.AFTER_EVALUATE_DECISION_SERVICE;
import static org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType.BEFORE_EVALUATE_DECISION_SERVICE;

public class TraceResourceId {

    private final String modelNamespace;
    private final String modelName;
    @JsonInclude(NON_NULL)
    private final String decisionServiceId;
    @JsonInclude(NON_NULL)
    private final String decisionServiceName;

    public TraceResourceId(String modelNamespace, String modelName) {
        this(modelNamespace, modelName, null, null);
    }

    public TraceResourceId(String modelNamespace, String modelName, String decisionServiceId, String decisionServiceName) {
        this.modelNamespace = modelNamespace;
        this.modelName = modelName;
        this.decisionServiceId = decisionServiceId;
        this.decisionServiceName = decisionServiceName;
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

    public static TraceResourceId from(DMNModel model) {
        if (model == null) {
            return null;
        }
        return new TraceResourceId(model.getNamespace(), model.getName());
    }

    public static TraceResourceId from(EvaluateEvent event) {
        if (event == null) {
            return null;
        }
        return event.getType() == BEFORE_EVALUATE_DECISION_SERVICE || event.getType() == AFTER_EVALUATE_DECISION_SERVICE
               ? new TraceResourceId(event.getModelNamespace(), event.getModelName(), event.getNodeId(), event.getNodeName())
               : new TraceResourceId(event.getModelNamespace(), event.getModelName());
    }
}

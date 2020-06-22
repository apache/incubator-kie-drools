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

import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType;

public enum TraceExecutionStepType {
    DMN_BKM_EVALUATION,
    DMN_BKM_INVOCATION,
    DMN_CONTEXT_ENTRY,
    DMN_DECISION,
    DMN_DECISION_SERVICE,
    DMN_DECISION_TABLE;

    public static TraceExecutionStepType from(EvaluateEventType type) {
        switch (type) {
            case BEFORE_EVALUATE_CONTEXT_ENTRY:
            case AFTER_EVALUATE_CONTEXT_ENTRY:
                return TraceExecutionStepType.DMN_CONTEXT_ENTRY;

            case BEFORE_EVALUATE_DECISION:
            case AFTER_EVALUATE_DECISION:
                return TraceExecutionStepType.DMN_DECISION;

            case BEFORE_EVALUATE_DECISION_SERVICE:
            case AFTER_EVALUATE_DECISION_SERVICE:
                return TraceExecutionStepType.DMN_DECISION_SERVICE;

            case BEFORE_EVALUATE_DECISION_TABLE:
            case AFTER_EVALUATE_DECISION_TABLE:
                return TraceExecutionStepType.DMN_DECISION_TABLE;

            case BEFORE_EVALUATE_BKM:
            case AFTER_EVALUATE_BKM:
                return TraceExecutionStepType.DMN_BKM_EVALUATION;

            case BEFORE_INVOKE_BKM:
            case AFTER_INVOKE_BKM:
                return TraceExecutionStepType.DMN_BKM_INVOCATION;

            default:
                return null;
        }
    }
}

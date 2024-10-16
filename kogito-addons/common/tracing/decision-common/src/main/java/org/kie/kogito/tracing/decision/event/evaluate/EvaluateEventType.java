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
package org.kie.kogito.tracing.decision.event.evaluate;

import org.kie.kogito.tracing.event.trace.TraceExecutionStepType;

public enum EvaluateEventType {
    AFTER_CONDITIONAL_EVALUATION(false),
    BEFORE_EVALUATE_ALL(true),
    AFTER_EVALUATE_ALL(false),
    BEFORE_EVALUATE_BKM(true),
    AFTER_EVALUATE_BKM(false),
    AFTER_EVALUATE_CONDITIONAL(false),
    BEFORE_EVALUATE_CONTEXT_ENTRY(true),
    AFTER_EVALUATE_CONTEXT_ENTRY(false),
    BEFORE_EVALUATE_DECISION(true),
    AFTER_EVALUATE_DECISION(false),
    BEFORE_EVALUATE_DECISION_SERVICE(true),
    AFTER_EVALUATE_DECISION_SERVICE(false),
    BEFORE_EVALUATE_DECISION_TABLE(true),
    AFTER_EVALUATE_DECISION_TABLE(false),
    BEFORE_INVOKE_BKM(true),
    AFTER_INVOKE_BKM(false);

    private final boolean before;

    EvaluateEventType(boolean before) {
        this.before = before;
    }

    public boolean isBefore() {
        return before;
    }

    public boolean isAfter() {
        return !before;
    }

    public TraceExecutionStepType toTraceExecutionStepType() {
        switch (this) {
            case AFTER_CONDITIONAL_EVALUATION:
            case AFTER_EVALUATE_CONDITIONAL:
                return TraceExecutionStepType.DMN_CONDITIONAL_INVOCATION;

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

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
package org.kie.kogito.tracing.decision.aggregator;

import java.util.LinkedList;
import java.util.List;

import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType;
import org.kie.kogito.tracing.event.trace.TraceExecutionStep;

/**
 * {@link DefaultAggregator} uses a stack to compute the list of {@link TraceExecutionStep}
 * with the correct parent-child relations.
 *
 * This class represents an entry in the aggregator stack, that will produce a {@link TraceExecutionStep}
 * object at the end of the computation.
 */
public class DefaultAggregatorStackEntry {

    private final EvaluateEvent beforeEvent;
    private final List<TraceExecutionStep> children;

    public DefaultAggregatorStackEntry(EvaluateEvent beforeEvent) {
        if (!beforeEvent.getType().isBefore()) {
            throw new IllegalStateException(String.format("%s is not a valid \"before\" event", beforeEvent.getType().name()));
        }
        this.beforeEvent = beforeEvent;
        this.children = new LinkedList<>();
    }

    public EvaluateEvent getBeforeEvent() {
        return beforeEvent;
    }

    public List<TraceExecutionStep> getChildren() {
        return children;
    }

    public void addChild(TraceExecutionStep child) {
        children.add(child);
    }

    public boolean isValidAfterEvent(EvaluateEvent afterEvent) {
        switch (afterEvent.getType()) {
            case AFTER_EVALUATE_CONTEXT_ENTRY:
                return beforeEvent.getType() == EvaluateEventType.BEFORE_EVALUATE_CONTEXT_ENTRY
                        && stringEquals(beforeEvent.getNodeName(), afterEvent.getNodeName());

            case AFTER_EVALUATE_DECISION_TABLE:
                return beforeEvent.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION_TABLE
                        && stringEquals(beforeEvent.getNodeName(), afterEvent.getNodeName());

            case AFTER_EVALUATE_DECISION:
                return beforeEvent.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION
                        && stringEquals(beforeEvent.getNodeId(), afterEvent.getNodeId());

            case AFTER_EVALUATE_DECISION_SERVICE:
                return beforeEvent.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION_SERVICE
                        && stringEquals(beforeEvent.getNodeId(), afterEvent.getNodeId());

            case AFTER_EVALUATE_BKM:
                return beforeEvent.getType() == EvaluateEventType.BEFORE_EVALUATE_BKM
                        && stringEquals(beforeEvent.getNodeId(), afterEvent.getNodeId());

            case AFTER_INVOKE_BKM:
                return beforeEvent.getType() == EvaluateEventType.BEFORE_INVOKE_BKM
                        && stringEquals(beforeEvent.getNodeId(), afterEvent.getNodeId());

            default:
                return false;
        }
    }

    public static boolean isValidBeforeEvent(EvaluateEvent beforeEvent) {
        switch (beforeEvent.getType()) {
            case BEFORE_EVALUATE_CONTEXT_ENTRY:
            case BEFORE_EVALUATE_DECISION_TABLE:
            case BEFORE_EVALUATE_DECISION:
            case BEFORE_EVALUATE_DECISION_SERVICE:
            case BEFORE_EVALUATE_BKM:
            case BEFORE_INVOKE_BKM:
                return true;

            default:
                return false;
        }
    }

    private static boolean stringEquals(String s1, String s2) {
        return (s1 == null && s2 == null) || (s1 != null && s1.equals(s2));
    }

}

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
package org.kie.kogito.tracing.decision.terminationdetector;

import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType;

public class BoundariesTerminationDetector implements TerminationDetector {

    EvaluateEvent firstEvent;
    boolean terminated;

    @Override
    public void add(EvaluateEvent event) {
        if (firstEvent == null) {
            if (isValidFirstEventType(event.getType())) {
                firstEvent = event;
            }
        } else {
            terminated = terminated || isValidLastEventType(event.getType());
        }
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    private boolean isValidFirstEventType(EvaluateEventType type) {
        switch (type) {
            case BEFORE_EVALUATE_ALL:
            case BEFORE_EVALUATE_DECISION_SERVICE:
                return true;

            default:
                return false;
        }
    }

    private boolean isValidLastEventType(EvaluateEventType type) {
        return firstEvent != null && (firstEvent.getType() == EvaluateEventType.BEFORE_EVALUATE_ALL && type == EvaluateEventType.AFTER_EVALUATE_ALL
                || firstEvent.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION_SERVICE && type == EvaluateEventType.AFTER_EVALUATE_DECISION_SERVICE);
    }
}

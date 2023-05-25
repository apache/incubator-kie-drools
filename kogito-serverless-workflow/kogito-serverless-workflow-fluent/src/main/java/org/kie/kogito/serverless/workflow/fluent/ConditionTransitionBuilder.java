/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.fluent;

import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.switchconditions.DataCondition;
import io.serverlessworkflow.api.transitions.Transition;

public class ConditionTransitionBuilder<T> extends TransitionBuilder<TransitionBuilder<T>> {

    private DataCondition condition;

    protected ConditionTransitionBuilder(TransitionBuilder<T> container, WorkflowBuilder workflow, DefaultState lastState, DataCondition condition) {
        super(container, workflow, lastState);
        this.condition = condition;
    }

    @Override
    protected void addTransition(DefaultState state) {
        if (condition != null) {
            condition.setTransition(new Transition(state.getName()));
            condition = null;
        } else {
            super.addTransition(state);
        }
    }

    @Override
    protected void addEnd(End end) {
        if (condition != null) {
            condition.setEnd(end);
            condition = null;
        } else {
            super.addEnd(end);
        }
    }

}

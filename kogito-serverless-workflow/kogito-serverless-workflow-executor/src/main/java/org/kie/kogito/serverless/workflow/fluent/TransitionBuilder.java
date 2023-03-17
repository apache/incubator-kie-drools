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

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.serverlessworkflow.api.defaultdef.DefaultConditionDefinition;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.states.SwitchState;
import io.serverlessworkflow.api.switchconditions.DataCondition;
import io.serverlessworkflow.api.transitions.Transition;

public class TransitionBuilder<T> {

    private final T container;
    private final Deque<DefaultState> states;
    private SwitchState switchState;

    protected TransitionBuilder(T container, Deque<DefaultState> states) {
        this.container = container;
        this.states = states;
    }

    public TransitionBuilder<T> next(StateBuilder<?, ?> stateBuilder) {
        next(stateBuilder.build());
        return this;
    }

    public TransitionBuilder<TransitionBuilder<T>> when(String expr) {
        List<DataCondition> conditions;
        if (switchState == null) {
            switchState = StateBuilder.ensureName(new SwitchState().withType(DefaultState.Type.SWITCH));
            conditions = new ArrayList<>();
            switchState.withDataConditions(conditions);
            next(switchState);
        } else {
            conditions = switchState.getDataConditions();
        }
        DataCondition condition = new DataCondition().withCondition(expr);
        conditions.add(condition);
        return new ConditionTransitionBuilder<>(this, states, condition);
    }

    public TransitionBuilder<T> or() {
        if (switchState == null) {
            throw new IllegalArgumentException("or should not be invoked before when");
        }
        DefaultConditionDefinition condition = new DefaultConditionDefinition();
        switchState.setDefaultCondition(condition);
        return new DefaultConditionTransitionBuilder<>(container, states, condition);
    }

    public T end(StateBuilder<?, ?> stateBuilder) {
        return end(stateBuilder, new End());
    }

    public T end(StateBuilder<?, ?> stateBuilder, End end) {
        next(stateBuilder.build(end));
        return container;
    }

    private void next(DefaultState state) {
        addTransition(state);
        states.add(state);
    }

    protected void addTransition(DefaultState state) {
        DefaultState prevState = states.getLast();
        prevState.setTransition(new Transition().withNextState(state.getName()));
    }
}

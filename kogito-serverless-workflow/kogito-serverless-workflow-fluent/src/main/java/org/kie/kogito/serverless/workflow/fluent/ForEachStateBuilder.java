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
import java.util.List;

import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.states.DefaultState.Type;
import io.serverlessworkflow.api.states.ForEachState;

public class ForEachStateBuilder extends StateBuilder<ForEachStateBuilder, ForEachState> {

    private List<Action> actions = new ArrayList<>();

    protected ForEachStateBuilder(String inputExpr) {
        super(new ForEachState().withType(Type.FOREACH).withInputCollection(inputExpr));
        state.withActions(actions);
    }

    public ForEachStateBuilder loopVar(String loopVar) {
        state.withIterationParam(loopVar);
        return this;
    }

    public ForEachStateBuilder outputCollection(String outputExpr) {
        state.withOutputCollection(outputExpr);
        return this;
    }

    public ForEachStateBuilder action(ActionBuilder builder) {
        builder.getFunction().ifPresent(functionDefinitions::add);
        builder.getEvent().ifPresent(eventDefinitions::add);
        actions.add(builder.build());
        return this;
    }
}

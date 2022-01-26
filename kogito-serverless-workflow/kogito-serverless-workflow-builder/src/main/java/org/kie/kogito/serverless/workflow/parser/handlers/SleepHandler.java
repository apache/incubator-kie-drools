/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.parser.handlers;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.states.SleepState;

public class SleepHandler extends StateHandler<SleepState> {

    protected SleepHandler(SleepState state, Workflow workflow, ParserContext parserContext) {
        super(state, workflow, parserContext);
    }

    @Override
    public boolean usedForCompensation() {
        return state.isUsedForCompensation();
    }

    @Override
    protected MakeNodeResult makeNode(RuleFlowNodeContainerFactory<?, ?> factory) {
        return new MakeNodeResult(factory.timerNode(parserContext.newId()).name(state.getName()).delay(state.getDuration()));
    }

}

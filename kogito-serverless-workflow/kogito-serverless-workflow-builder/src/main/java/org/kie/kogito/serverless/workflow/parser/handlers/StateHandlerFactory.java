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

import java.util.Optional;

import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.states.CallbackState;
import io.serverlessworkflow.api.states.DelayState;
import io.serverlessworkflow.api.states.EventState;
import io.serverlessworkflow.api.states.InjectState;
import io.serverlessworkflow.api.states.OperationState;
import io.serverlessworkflow.api.states.ParallelState;
import io.serverlessworkflow.api.states.SubflowState;
import io.serverlessworkflow.api.states.SwitchState;

public class StateHandlerFactory {

    private StateHandlerFactory() {
    }

    private static Logger logger = LoggerFactory.getLogger(StateHandlerFactory.class);

    @SuppressWarnings("unchecked")
    public static <S extends State, T extends NodeFactory<T, RuleFlowProcessFactory>> Optional<StateHandler<S, T, RuleFlowProcessFactory>> getStateHandler(S state, Workflow workflow,
            RuleFlowProcessFactory factory, ParserContext parserContext) {
        StateHandler<S, T, RuleFlowProcessFactory> result;
        switch (state.getType()) {
            case EVENT:
                result = (StateHandler<S, T, RuleFlowProcessFactory>) new EventHandler<>((EventState) state, workflow, factory, parserContext);
                break;
            case OPERATION:
                result = (StateHandler<S, T, RuleFlowProcessFactory>) new OperationHandler<>((OperationState) state, workflow, factory, parserContext);
                break;
            case DELAY:
                result = (StateHandler<S, T, RuleFlowProcessFactory>) new DelayHandler<>((DelayState) state, workflow, factory, parserContext);
                break;
            case INJECT:
                result = (StateHandler<S, T, RuleFlowProcessFactory>) new InjectHandler<>((InjectState) state, workflow, factory, parserContext);
                break;
            case SUBFLOW:
                result = (StateHandler<S, T, RuleFlowProcessFactory>) new SubflowHandler<>((SubflowState) state, workflow, factory, parserContext);
                break;
            case SWITCH:
                result = (StateHandler<S, T, RuleFlowProcessFactory>) new SwitchHandler<>((SwitchState) state, workflow, factory, parserContext);
                break;
            case PARALLEL:
                result = (StateHandler<S, T, RuleFlowProcessFactory>) new ParallelHandler<>((ParallelState) state, workflow, factory, parserContext);
                break;
            case CALLBACK:
                result = (StateHandler<S, T, RuleFlowProcessFactory>) new CallbackHandler<>((CallbackState) state, workflow, factory, parserContext);
                break;
            default:
                logger.warn("Unsupported state {}. Ignoring it", state.getName());
                return Optional.empty();
        }
        parserContext.add(result);
        return Optional.of(result);
    }

}

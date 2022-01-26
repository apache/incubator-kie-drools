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

import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.states.CallbackState;
import io.serverlessworkflow.api.states.EventState;
import io.serverlessworkflow.api.states.ForEachState;
import io.serverlessworkflow.api.states.InjectState;
import io.serverlessworkflow.api.states.OperationState;
import io.serverlessworkflow.api.states.ParallelState;
import io.serverlessworkflow.api.states.SleepState;
import io.serverlessworkflow.api.states.SwitchState;

public class StateHandlerFactory {

    private StateHandlerFactory() {
    }

    private static Logger logger = LoggerFactory.getLogger(StateHandlerFactory.class);

    public static Optional<StateHandler<?>> getStateHandler(State state, Workflow workflow, ParserContext parserContext) {
        StateHandler<?> result;
        switch (state.getType()) {
            case EVENT:
                result = new EventHandler((EventState) state, workflow, parserContext);
                break;
            case OPERATION:
                result = new OperationHandler((OperationState) state, workflow, parserContext);
                break;
            case SLEEP:
                result = new SleepHandler((SleepState) state, workflow, parserContext);
                break;
            case INJECT:
                result = new InjectHandler((InjectState) state, workflow, parserContext);
                break;
            case SWITCH:
                result = new SwitchHandler((SwitchState) state, workflow, parserContext);
                break;
            case PARALLEL:
                result = new ParallelHandler((ParallelState) state, workflow, parserContext);
                break;
            case CALLBACK:
                result = new CallbackHandler((CallbackState) state, workflow, parserContext);
                break;
            case FOREACH:
                result = new ForEachStateHandler((ForEachState) state, workflow, parserContext);
                break;
            default:
                logger.warn("Unsupported state {}. Ignoring it", state.getName());
                return Optional.empty();
        }
        parserContext.add(result);
        return Optional.of(result);
    }

}

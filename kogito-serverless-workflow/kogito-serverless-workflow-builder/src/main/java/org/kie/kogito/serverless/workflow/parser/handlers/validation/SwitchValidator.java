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
package org.kie.kogito.serverless.workflow.parser.handlers.validation;

import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.defaultdef.DefaultConditionDefinition;
import io.serverlessworkflow.api.states.SwitchState;
import io.serverlessworkflow.api.transitions.Transition;

import static org.kie.kogito.serverless.workflow.utils.TimeoutsConfigResolver.resolveEventTimeout;

public class SwitchValidator {

    static final String EVENT_TIMEOUT_REQUIRED_ERROR =
            "The \"eventTimeout\" configuration is required for the \"eventConditions\" based switch state \"%s\" that belongs to the serverless workflow: \"%s\".";

    static final String NEXT_STATE_REQUIRED_FOR_DEFAULT_CONDITION_ERROR =
            "The \"nextState\" is required for the \"defaultCondition\" transition in the switch state \"%s\" that belongs to the serverless workflow: \"%s\".";

    static final String NEXT_STATE_NOT_FOUND_FOR_DEFAULT_CONDITION_ERROR =
            "The \"nextState\" : \"%s\" configured for the \"defaultCondition\" transition in the switch state \"%s\", was not found in the serverless workflow: \"%s\".";

    static final String TRANSITION_OR_END_MUST_BE_CONFIGURED_FOR_DEFAULT_CONDITION_ERROR =
            "The \"defaultCondition\" in the switch state \"%s\" that belongs to the serverless workflow: \"%s\", must either have a configured \"transition\" or be set as \"end\" : true.";

    static final String CONDITIONS_NOT_FOUND_ERROR =
            "No dataConditions or eventConditions where found for the switch state \"%s\" that belongs to the serverless workflow: \"%s\".";

    static final String DATA_CONDITIONS_AND_EVENT_CONDITIONS_FOUND_ERROR =
            "DataConditions and eventConditions where found at the same time for the switch state \"%s\" that belongs to the serverless workflow: \"%s\".";

    private SwitchValidator() {
    }

    public static void validateConditions(SwitchState state, Workflow workflow, ParserContext context) {
        if (state.getDataConditions().isEmpty() && state.getEventConditions().isEmpty()) {
            context.addValidationError(String.format(CONDITIONS_NOT_FOUND_ERROR, state.getName(), workflow.getName()));
        } else if (!state.getDataConditions().isEmpty() && !state.getEventConditions().isEmpty()) {
            context.addValidationError(String.format(DATA_CONDITIONS_AND_EVENT_CONDITIONS_FOUND_ERROR, state.getName(), workflow.getName()));
        }
    }

    public static void validateDefaultCondition(DefaultConditionDefinition defaultCondition,
            SwitchState state,
            Workflow workflow,
            ParserContext parserContext) {
        Transition transition = defaultCondition.getTransition();
        if (transition != null) {
            String nextState = transition.getNextState();
            if (nextState == null || nextState.isEmpty()) {
                parserContext.addValidationError(String.format(NEXT_STATE_REQUIRED_FOR_DEFAULT_CONDITION_ERROR, state.getName(), workflow.getName()));
            } else if (parserContext.getStateHandler(nextState) == null) {
                parserContext.addValidationError(String.format(NEXT_STATE_NOT_FOUND_FOR_DEFAULT_CONDITION_ERROR, nextState, state.getName(), workflow.getName()));
            }
        } else if (defaultCondition.getEnd() == null) {
            parserContext.addValidationError(String.format(TRANSITION_OR_END_MUST_BE_CONFIGURED_FOR_DEFAULT_CONDITION_ERROR, state.getName(), workflow.getName()));
        }
        if (!state.getEventConditions().isEmpty()) {
            String eventTimeout = resolveEventTimeout(state, workflow);
            if (eventTimeout == null) {
                parserContext.addValidationError(String.format(EVENT_TIMEOUT_REQUIRED_ERROR, state.getName(), workflow.getName()));
            }
        }
    }
}

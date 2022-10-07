/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serverless.workflow.parser.handlers.validation;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.handlers.StateHandler;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.defaultdef.DefaultConditionDefinition;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.states.SwitchState;
import io.serverlessworkflow.api.switchconditions.DataCondition;
import io.serverlessworkflow.api.switchconditions.EventCondition;
import io.serverlessworkflow.api.transitions.Transition;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.serverless.workflow.parser.handlers.validation.SwitchValidator.CONDITIONS_NOT_FOUND_ERROR;
import static org.kie.kogito.serverless.workflow.parser.handlers.validation.SwitchValidator.DATA_CONDITIONS_AND_EVENT_CONDITIONS_FOUND_ERROR;
import static org.kie.kogito.serverless.workflow.parser.handlers.validation.SwitchValidator.EVENT_TIMEOUT_REQUIRED_ERROR;
import static org.kie.kogito.serverless.workflow.parser.handlers.validation.SwitchValidator.NEXT_STATE_NOT_FOUND_FOR_DEFAULT_CONDITION_ERROR;
import static org.kie.kogito.serverless.workflow.parser.handlers.validation.SwitchValidator.NEXT_STATE_REQUIRED_FOR_DEFAULT_CONDITION_ERROR;
import static org.kie.kogito.serverless.workflow.parser.handlers.validation.SwitchValidator.TRANSITION_OR_END_MUST_BE_CONFIGURED_FOR_DEFAULT_CONDITION_ERROR;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SwitchValidatorTest {

    private static final String WORKFLOW_NAME = "WORKFLOW_NAME";
    private static final String SWITCH_STATE_NAME = "SWITCH_STATE_NAME";
    private static final String NEXT_STATE = "NEXT_STATE";

    private Workflow workflow;
    private SwitchState switchState;
    private ParserContext parserContext;

    @BeforeEach
    void setUp() {
        workflow = mockWorkflow();
        switchState = mockSwitchState();
        parserContext = mock(ParserContext.class);
    }

    @Test
    void validateConditionsNoConditionsFoundError() {
        assertThatThrownBy(() -> SwitchValidator.validateConditions(switchState, workflow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(CONDITIONS_NOT_FOUND_ERROR, SWITCH_STATE_NAME, WORKFLOW_NAME));
    }

    @Test
    void validateConditionsBothConditionsFoundError() {
        switchState.getDataConditions().add(mock(DataCondition.class));
        switchState.getEventConditions().add(mock(EventCondition.class));
        assertThatThrownBy(() -> SwitchValidator.validateConditions(switchState, workflow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(DATA_CONDITIONS_AND_EVENT_CONDITIONS_FOUND_ERROR, SWITCH_STATE_NAME, WORKFLOW_NAME));
    }

    @Test
    void validateDefaultConditionTransitionWithoutNextError() {
        DefaultConditionDefinition defaultCondition = mockDefaultConditionWithTransition();
        assertThatThrownBy(() -> SwitchValidator.validateDefaultCondition(defaultCondition, switchState, workflow, parserContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(NEXT_STATE_REQUIRED_FOR_DEFAULT_CONDITION_ERROR, SWITCH_STATE_NAME, WORKFLOW_NAME));
    }

    @Test
    void validateDefaultConditionTransitionNextStateNotFoundError() {
        DefaultConditionDefinition defaultCondition = mockDefaultConditionWithTransition();
        Transition transition = defaultCondition.getTransition();
        doReturn(NEXT_STATE).when(transition).getNextState();
        assertThatThrownBy(() -> SwitchValidator.validateDefaultCondition(defaultCondition, switchState, workflow, parserContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(NEXT_STATE_NOT_FOUND_FOR_DEFAULT_CONDITION_ERROR, NEXT_STATE, SWITCH_STATE_NAME, WORKFLOW_NAME));
    }

    @Test
    void validateDefaultConditionWithoutTransitionAndEndIsNullError() {
        DefaultConditionDefinition defaultCondition = mock(DefaultConditionDefinition.class);
        assertThatThrownBy(() -> SwitchValidator.validateDefaultCondition(defaultCondition, switchState, workflow, parserContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(TRANSITION_OR_END_MUST_BE_CONFIGURED_FOR_DEFAULT_CONDITION_ERROR, SWITCH_STATE_NAME, WORKFLOW_NAME));
    }

    @Test
    void validateDefaultConditionWithEventConditionsTransitionButTimeoutNotSetError() {
        switchState.getEventConditions().add(mock(EventCondition.class));
        DefaultConditionDefinition defaultCondition = mockDefaultConditionWithTransition();
        Transition transition = defaultCondition.getTransition();
        doReturn(NEXT_STATE).when(transition).getNextState();
        StateHandler<?> stateHandler = mock(StateHandler.class);
        doReturn(stateHandler).when(parserContext).getStateHandler(NEXT_STATE);
        assertThatThrownBy(() -> SwitchValidator.validateDefaultCondition(defaultCondition, switchState, workflow, parserContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(EVENT_TIMEOUT_REQUIRED_ERROR, SWITCH_STATE_NAME, WORKFLOW_NAME));
    }

    @Test
    void validateDefaultConditionWithEventConditionsEndButTimeoutNotSetError() {
        switchState.getEventConditions().add(mock(EventCondition.class));
        DefaultConditionDefinition defaultCondition = mock(DefaultConditionDefinition.class);
        End end = mock(End.class);
        doReturn(end).when(defaultCondition).getEnd();
        assertThatThrownBy(() -> SwitchValidator.validateDefaultCondition(defaultCondition, switchState, workflow, parserContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(EVENT_TIMEOUT_REQUIRED_ERROR, SWITCH_STATE_NAME, WORKFLOW_NAME));
    }

    private SwitchState mockSwitchState() {
        SwitchState switchState = mock(SwitchState.class);
        doReturn(SWITCH_STATE_NAME).when(switchState).getName();
        List<DataCondition> dataConditions = new ArrayList<>();
        doReturn(dataConditions).when(switchState).getDataConditions();
        List<EventCondition> eventConditions = new ArrayList<>();
        doReturn(eventConditions).when(switchState).getEventConditions();
        return switchState;
    }

    private Workflow mockWorkflow() {
        Workflow workflow = mock(Workflow.class);
        doReturn(WORKFLOW_NAME).when(workflow).getName();
        return workflow;
    }

    private static DefaultConditionDefinition mockDefaultConditionWithTransition() {
        DefaultConditionDefinition defaultCondition = mock(DefaultConditionDefinition.class);
        Transition transition = mock(Transition.class);
        doReturn(transition).when(defaultCondition).getTransition();
        return defaultCondition;
    }

}

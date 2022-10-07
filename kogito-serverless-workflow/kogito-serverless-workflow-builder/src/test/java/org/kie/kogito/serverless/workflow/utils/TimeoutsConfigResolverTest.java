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

package org.kie.kogito.serverless.workflow.utils;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.timeouts.TimeoutsDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class TimeoutsConfigResolverTest {

    private static final String STATE_NAME = "STATE_NAME";
    private static final String WORKFLOW_NAME = "WORKFLOW_NAME";
    private static final String VALID_STATE_TIMEOUT_DURATION = "PT1M";
    private static final String VALID_WORKFLOW_TIMEOUT_DURATION = "PT8M";
    private static final String INVALID_STATE_TIMEOUT_DURATION = "INVALID_STATE_TIMEOUT_DURATION";
    private static final String EMPTY_STATE_TIMEOUT_DURATION = "";
    private static final String INVALID_WORKFLOW_TIMEOUT_DURATION = "INVALID_WORKFLOW_TIMEOUT_DURATION";
    private static final String EMPTY_WORKFLOW_TIMEOUT_DURATION = "";

    @ParameterizedTest
    @MethodSource("successfulCaseParams")
    void resolveEventTimeoutSuccessful(State state, Workflow workflow, String expectedTimeout) {
        assertThat(TimeoutsConfigResolver.resolveEventTimeout(state, workflow)).isEqualTo(expectedTimeout);
    }

    static Stream<Arguments> successfulCaseParams() {
        return Stream.of(
                Arguments.of(mockState(STATE_NAME), mockWorkflow(WORKFLOW_NAME), null),
                Arguments.of(mockState(STATE_NAME, null), mockWorkflow(WORKFLOW_NAME, null), null),
                Arguments.of(mockState(STATE_NAME, VALID_STATE_TIMEOUT_DURATION), mockWorkflow(WORKFLOW_NAME), VALID_STATE_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, VALID_STATE_TIMEOUT_DURATION), mockWorkflow(WORKFLOW_NAME, null), VALID_STATE_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, VALID_STATE_TIMEOUT_DURATION), mockWorkflow(WORKFLOW_NAME, VALID_WORKFLOW_TIMEOUT_DURATION), VALID_STATE_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME), mockWorkflow(WORKFLOW_NAME, VALID_WORKFLOW_TIMEOUT_DURATION), VALID_WORKFLOW_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, null), mockWorkflow(WORKFLOW_NAME, VALID_WORKFLOW_TIMEOUT_DURATION), VALID_WORKFLOW_TIMEOUT_DURATION));
    }

    @ParameterizedTest
    @MethodSource("unsuccessfulCaseParams")
    void resolveEventTimeoutUnsuccessful(State state, Workflow workflow, String expectedMessagePart) {
        assertThatThrownBy(() -> TimeoutsConfigResolver.resolveEventTimeout(state, workflow))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessagePart);
    }

    static Stream<Arguments> unsuccessfulCaseParams() {
        return Stream.of(
                Arguments.of(mockState(STATE_NAME, INVALID_STATE_TIMEOUT_DURATION), mockWorkflow(WORKFLOW_NAME), INVALID_STATE_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, EMPTY_STATE_TIMEOUT_DURATION), mockWorkflow(WORKFLOW_NAME), EMPTY_STATE_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, INVALID_STATE_TIMEOUT_DURATION), mockWorkflow(WORKFLOW_NAME, null), INVALID_STATE_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, INVALID_STATE_TIMEOUT_DURATION), mockWorkflow(WORKFLOW_NAME, INVALID_WORKFLOW_TIMEOUT_DURATION), INVALID_STATE_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, INVALID_STATE_TIMEOUT_DURATION), mockWorkflow(WORKFLOW_NAME, VALID_WORKFLOW_TIMEOUT_DURATION), INVALID_STATE_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME), mockWorkflow(WORKFLOW_NAME, INVALID_WORKFLOW_TIMEOUT_DURATION), INVALID_WORKFLOW_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME), mockWorkflow(WORKFLOW_NAME, EMPTY_WORKFLOW_TIMEOUT_DURATION), EMPTY_WORKFLOW_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, null), mockWorkflow(WORKFLOW_NAME, INVALID_WORKFLOW_TIMEOUT_DURATION), INVALID_WORKFLOW_TIMEOUT_DURATION),
                Arguments.of(mockState(STATE_NAME, null), mockWorkflow(WORKFLOW_NAME, INVALID_WORKFLOW_TIMEOUT_DURATION), INVALID_WORKFLOW_TIMEOUT_DURATION));
    }

    private static State mockState(String name) {
        State state = mock(State.class);
        doReturn(name).when(state).getName();
        return state;
    }

    private static State mockState(String name, String eventTimeout) {
        State state = mockState(name);
        TimeoutsDefinition timeoutsDefinition = mockTimeoutsDefinition(eventTimeout);
        doReturn(timeoutsDefinition).when(state).getTimeouts();
        return state;
    }

    private static Workflow mockWorkflow(String name) {
        Workflow workflow = mock(Workflow.class);
        doReturn(name).when(workflow).getName();
        return workflow;
    }

    private static Workflow mockWorkflow(String name, String eventTimeout) {
        Workflow workflow = mockWorkflow(name);
        TimeoutsDefinition timeoutsDefinition = mockTimeoutsDefinition(eventTimeout);
        doReturn(timeoutsDefinition).when(workflow).getTimeouts();
        return workflow;
    }

    private static TimeoutsDefinition mockTimeoutsDefinition(String eventTimeout) {
        TimeoutsDefinition timeoutsDefinition = mock(TimeoutsDefinition.class);
        doReturn(eventTimeout).when(timeoutsDefinition).getEventTimeout();
        return timeoutsDefinition;
    }
}

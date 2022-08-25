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
package org.kie.kogito.codegen.tests;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.codegen.process.ProcessCodegenException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvalidProcessIT extends AbstractCodegenIT {

    //Process Validations Tests
    static Stream<Arguments> invalidProcessesForTest() {
        return Stream.of(
                Arguments.of("invalid/parsing-more-than-one-start.bpmn2"),
                Arguments.of("invalid/validator-no-start.bpmn2"),
                Arguments.of("invalid/parsing-multi-connection-end.bpmn2"),
                Arguments.of("invalid/validator-no-end.bpmn2"),
                Arguments.of("invalid/parsing-service-task-no-impl.bpmn2"));
    }

    static Stream<Arguments> invalidDataMappingProcessesForTest() {
        return Stream.of(
                Arguments.of(new String[] { "invalid/invalid-message-end-event.bpmn2",
                        "Errors during validation for processes [MessageEndEvent]",
                        "Node 'processedconsumers' [2] Source variable 'customerId':'java.lang.String' has different data type from 'event':'java.lang.Boolean' in data input assignment" }),
                Arguments.of(new String[] { "invalid/invalid-message-start-event.bpmn2",
                        "Errors during validation for processes [MessageStartEvent]",
                        "Node 'StartProcess' [1] Target variable 'customerId':'java.lang.String' has different data type from 'event':'java.lang.Boolean' in data output assignment" }),
                Arguments.of(new String[] { "invalid/intermediate-throw-event-message.bpmn2",
                        "Errors during validation for processes [IntermediateThrowEventMessage]",
                        "Node 'Intermediate Throw Event 1' [3] Source variable 'customerId':'java.lang.String' has different data type from 'input':'java.lang.Float' in data input assignment" }),
                Arguments.of(new String[] { "invalid/intermediate-catch-event-message.bpmn2",
                        "Errors during validation for processes [IntermediateCatchEventMessage]",
                        "Node 'Intermediate Catch Event' [2] Target variable 'customerId':'java.lang.String' has different data type from 'event':'org.acme.Customer' in data output assignment" }));
    }

    @Test
    public void testBasicUserTaskProcess() {
        assertThrows(IllegalArgumentException.class,
                () -> generateCodeProcessesOnly("invalid/invalid-process-id.bpmn2"),
                "Process id '_7063C749-BCA8-4B6D-BC31-ACEE6FDF5512' is not valid");
    }

    @Test
    public void testDuplicatedProcessId() {
        assertThrows(ProcessCodegenException.class,
                () -> generateCodeProcessesOnly("invalid/duplicated-process-id-1.bpmn2", "invalid/duplicated-process-id-2.bpmn2"),
                "Duplicated process with id duplicated found in the project, please review .bpmn files");
    }

    @ParameterizedTest
    @MethodSource("invalidProcessesForTest")
    public void testInvalidProcess(String processFile) {
        assertThatThrownBy(() -> generateCodeProcessesOnly(processFile))
                .isInstanceOf(ProcessCodegenException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidDataMappingProcessesForTest")
    public void testInvalidDataMappingProcesses(String processFile, String message, String rootCauseMessage) {
        assertThatThrownBy(() -> generateCodeProcessesOnly(processFile))
                .isInstanceOf(ProcessCodegenException.class)
                .hasMessage(message)
                .hasRootCauseMessage(rootCauseMessage)
                .getRootCause();
    }

}

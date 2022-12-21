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
package org.kie.kogito.addons.quarkus.camel.deployment;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.states.CallbackState;
import io.serverlessworkflow.api.states.ForEachState;
import io.serverlessworkflow.api.states.OperationState;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.kogito.addons.quarkus.camel.deployment.CamelFunctionStaticValidator.validateFunctionRef;
import static org.kie.kogito.quarkus.serverless.workflow.WorkflowCodeGenUtils.getWorkflows;

public class CamelFunctionStaticValidatorTest {

    @ParameterizedTest
    @CsvSource({
            "valid-camel-soap.sw.json,callSoap",
            "valid-newsletter-subscription.sw.json,subscribeToNewsletter",
            "valid-camel-soap-no-args.sw.json,callSoap",
            "valid-camel-soap-expression-args.sw.json,callSoap",
            "valid-camel-soap-headers.sw.json,callSoap",
    })
    public void verifyValidWorkflowsWithCamelFunctions(final String workflowFile, final String functionDefName) throws URISyntaxException {
        getWorkflows(Stream.of(Paths.get(requireNonNull(CamelFunctionStaticValidatorTest.class.getResource("/" + workflowFile)).toURI())))
                .forEach(w -> verifyWorkflow(w, functionDefName));
    }

    @ParameterizedTest
    @CsvSource({ "invalid-camel-soap.sw.json,callSoap",
            "invalid-camel-soap-headers-array.sw.json,callSoap",
            "invalid-camel-soap-headers-primitive.sw.json,callSoap" })
    public void verifyInvalidWorkflowsWithCamelFunctions(final String workflowFile, final String functionDefName) throws URISyntaxException {
        getWorkflows(Stream.of(
                Paths.get(requireNonNull(CamelFunctionStaticValidatorTest.class.getResource("/" + workflowFile)).toURI())))
                        .forEach(w -> assertThrows(IllegalArgumentException.class, () -> verifyWorkflow(w, functionDefName)));
    }

    private void verifyWorkflow(final Workflow workflow, final String functionDefName) {
        workflow.getStates().forEach(s -> {
            switch (s.getType()) {
                case OPERATION:
                    ((OperationState) s).getActions().forEach(a -> verifyAction(a, functionDefName));
                    break;
                case FOREACH:
                    ((ForEachState) s).getActions().forEach(a -> verifyAction(a, functionDefName));
                    break;
                case CALLBACK:
                    verifyAction(((CallbackState) s).getAction(), functionDefName);
                    break;
                // Sonar compliance
                default:
                    break;
            }
        });
    }

    private void verifyAction(final Action action, final String functionDefName) {
        if (functionDefName.equals(action.getFunctionRef().getRefName())) {
            validateFunctionRef(action.getFunctionRef());
        }
    }

}

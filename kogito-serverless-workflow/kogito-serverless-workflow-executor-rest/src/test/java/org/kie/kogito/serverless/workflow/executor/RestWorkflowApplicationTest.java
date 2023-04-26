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
package org.kie.kogito.serverless.workflow.executor;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState.Type;
import io.serverlessworkflow.api.states.OperationState;
import io.serverlessworkflow.api.workflow.Functions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class RestWorkflowApplicationTest {

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Test
    void restInvocation() {
        JsonNode expectedOutput = ObjectMapperFactory.get().createObjectNode().put("name", "Javierito");
        wm.stubFor(get("/name").willReturn(aResponse().withStatus(200).withJsonBody(expectedOutput)));
        final String START_STATE = "start";
        final String FUNCTION_NAME = "function";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = new Workflow("HelloRest", "Hello Rest", "1.0", Arrays.asList(
                    new OperationState().withName(START_STATE).withType(Type.OPERATION).withActions(Arrays.asList(new Action().withFunctionRef(new FunctionRef(FUNCTION_NAME)))).withEnd(new End())))
                            .withStart(new Start().withStateName(START_STATE))
                            .withFunctions(new Functions(Arrays.asList(new FunctionDefinition(FUNCTION_NAME).withOperation("rest:get:http://localhost:" + wm.getPort() + "/name")
                                    .withType(FunctionDefinition.Type.CUSTOM))));
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).isEqualTo(expectedOutput);
        }
    }
}

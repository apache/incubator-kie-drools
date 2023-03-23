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

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.utils.WorkflowFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState.Type;
import io.serverlessworkflow.api.states.InjectState;
import io.serverlessworkflow.api.states.OperationState;
import io.serverlessworkflow.api.workflow.Functions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.getWorkflow;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.writeWorkflow;

class StaticWorkflowApplicationTest {

    private static final String GREETING_STRING = "Hello World!!!";
    private static final String START_STATE = "start";

    @Test
    void helloWorld() {
        Workflow workflow = helloWorldDef();
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).contains(new TextNode(GREETING_STRING));
        }
    }

    @Test
    void helloWorldFile() throws IOException {
        Workflow workflow = helloWorldDef();
        CharArrayWriter writer = new CharArrayWriter();
        writeWorkflow(workflow, writer, WorkflowFormat.JSON);
        CharArrayReader reader = new CharArrayReader(writer.toCharArray());
        workflow = getWorkflow(reader, WorkflowFormat.JSON);
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).contains(new TextNode(GREETING_STRING));
        }
    }

    private Workflow helloWorldDef() {
        return new Workflow("HelloWorld", "Hello World", "1.0", Arrays.asList(
                new InjectState(START_STATE, Type.INJECT).withData(new TextNode(GREETING_STRING)).withEnd(new End())))
                        .withStart(new Start().withStateName(START_STATE));
    }

    @Test
    void restInvocation() {
        WireMockServer server = new WireMockServer();
        JsonNode expectedOutput = ObjectMapperFactory.get().createObjectNode().put("name", "Javierito");
        server.stubFor(get("/name").willReturn(aResponse().withStatus(200).withJsonBody(expectedOutput)));
        final String START_STATE = "start";
        final String FUNCTION_NAME = "function";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            server.start();
            Workflow workflow = new Workflow("HelloRest", "Hello Rest", "1.0", Arrays.asList(
                    new OperationState().withName(START_STATE).withType(Type.OPERATION).withActions(Arrays.asList(new Action().withFunctionRef(new FunctionRef(FUNCTION_NAME)))).withEnd(new End())))
                            .withStart(new Start().withStateName(START_STATE))
                            .withFunctions(new Functions(Arrays.asList(new FunctionDefinition(FUNCTION_NAME).withOperation("rest:get:http://localhost:" + server.getOptions().portNumber() + "/name")
                                    .withType(FunctionDefinition.Type.CUSTOM))));
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).isEqualTo(expectedOutput);
        } finally {
            server.stop();
        }
    }
}

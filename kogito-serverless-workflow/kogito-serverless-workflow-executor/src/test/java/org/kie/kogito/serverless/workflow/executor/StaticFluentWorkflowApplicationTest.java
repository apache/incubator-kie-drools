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
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.HttpMethod;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.serverlessworkflow.api.Workflow;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.expr;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.rest;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.forEach;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.inject;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.arrayNode;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.objectNode;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

class StaticFluentWorkflowApplicationTest {

    @Test
    void helloWorld() {
        final String GREETING_STRING = "Hello World!!!";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("HelloWorld").singleton(inject(new TextNode(GREETING_STRING)));
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).contains(new TextNode(GREETING_STRING));
        }
    }

    @Test
    void restInvocation() {
        WireMockServer server = new WireMockServer();
        JsonNode expectedOutput = ObjectMapperFactory.get().createObjectNode().put("name", "Javierito");
        server.stubFor(get("/name").willReturn(aResponse().withStatus(200).withJsonBody(expectedOutput)));
        final String FUNCTION_NAME = "function";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            server.start();
            Workflow workflow = workflow("HelloRest").function(rest(FUNCTION_NAME, HttpMethod.get, "http://localhost:" + server.getOptions().portNumber() + "/name"))
                    .singleton(operation().action(call(FUNCTION_NAME)));
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).isEqualTo(expectedOutput);
        } finally {
            server.stop();
        }
    }

    @Test
    void testExpr() {
        final String DOUBLE = "double";
        final String SQUARE = "square";
        final String HALF = "half";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PlayingWithExpression").function(expr(DOUBLE, ".input*=2")).function(expr(SQUARE, ".input*=.input")).function(expr(HALF, ".input/=2"))
                    .start(operation().action(call(DOUBLE)).action(call(SQUARE)).action(call(HALF)))
                    .end(operation().outputFilter("{result:.input}")).build();
            assertThat(application.execute(workflow, Collections.singletonMap("input", 4)).getWorkflowdata().get("result").asInt()).isEqualTo(32);
        }
    }

    @Test
    void testForEach() {
        final String SQUARE = "square";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("ForEachTest").function(expr(SQUARE, ".input*.input"))
                    .singleton(forEach(".numbers").loopVar("input").outputCollection(".result").action(call(SQUARE)));
            assertThat(application.execute(workflow, Collections.singletonMap("numbers", Arrays.asList(1, 2, 3, 4))).getWorkflowdata().get("result"))
                    .isEqualTo(arrayNode().add(1).add(4).add(9).add(16));
        }
    }

    @Test
    void testSwitch() {
        final String DOUBLE = "double";
        final String SQUARE = "square";
        final String HALF = "half";
        final String EVEN = "Event result";
        final String ODD = "Event result";
        final String MESSAGE = "message";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("SwitchTest").function(expr(DOUBLE, ".input*=2")).function(expr(SQUARE, ".input*=.input")).function(expr(HALF, ".input/=2"))
                    .start(operation().action(call(DOUBLE)).action(call(SQUARE)).action(call(HALF)))
                    .when(".input%2==0").end(inject(objectNode().put(MESSAGE, EVEN)))
                    .or().end(inject(objectNode().put(MESSAGE, ODD))).build();
            Process<JsonNodeModel> process = application.process(workflow);
            assertThat(application.execute(process, Collections.singletonMap("input", 4)).getWorkflowdata().get(MESSAGE).asText()).isEqualTo(ODD);
            assertThat(application.execute(process, Collections.singletonMap("input", 7)).getWorkflowdata().get(MESSAGE).asText()).isEqualTo(EVEN);
        }
    }
}

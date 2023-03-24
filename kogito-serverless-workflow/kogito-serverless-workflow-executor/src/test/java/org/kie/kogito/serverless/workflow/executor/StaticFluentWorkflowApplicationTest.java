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
import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.actions.WorkflowLogLevel;
import org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.HttpMethod;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import io.serverlessworkflow.api.Workflow;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.log;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.expr;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.java;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.log;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.rest;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.forEach;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.inject;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.parallel;
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

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Test
    void restInvocation() {
        JsonNode expectedOutput = ObjectMapperFactory.get().createObjectNode().put("name", "Javierito");
        wm.stubFor(get("/name").willReturn(aResponse().withStatus(200).withJsonBody(expectedOutput)));
        final String FUNCTION_NAME = "function";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("HelloRest").function(rest(FUNCTION_NAME, HttpMethod.get, "http://localhost:" + wm.getPort() + "/name"))
                    .singleton(operation().action(call(FUNCTION_NAME)));
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).isEqualTo(expectedOutput);
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
        final String LOG_INFO = "logInfo";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow =
                    workflow("SwitchTest").function(log(LOG_INFO, WorkflowLogLevel.INFO)).function(expr(DOUBLE, ".input*=2")).function(expr(SQUARE, ".input*=.input")).function(expr(HALF, ".input/=2"))
                            .start(operation().action(call(DOUBLE)).action(call(SQUARE)).action(call(HALF)))
                            .next(operation().action(log(LOG_INFO, "\"Input is \\(.input)\"")))
                            .when(".input%2==0").end(inject(objectNode().put(MESSAGE, EVEN)))
                            .or().end(inject(objectNode().put(MESSAGE, ODD))).build();
            Process<JsonNodeModel> process = application.process(workflow);
            assertThat(application.execute(process, Collections.singletonMap("input", 4)).getWorkflowdata().get(MESSAGE).asText()).isEqualTo(ODD);
            assertThat(application.execute(process, Collections.singletonMap("input", 7)).getWorkflowdata().get(MESSAGE).asText()).isEqualTo(EVEN);
        }
    }

    @Test
    void testParallel() {
        final String DOUBLE = "double";
        final String HALF = "half";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("ParallelTest").function(expr(DOUBLE, ".input*2")).function(expr(HALF, ".input/2"))
                    .singleton(parallel()
                            .newBranch().action(call(DOUBLE).outputFilter(".double")).endBranch()
                            .newBranch().action(call(HALF).outputFilter(".half")).endBranch());

            Process<JsonNodeModel> process = application.process(workflow);
            JsonNode result = application.execute(process, Collections.singletonMap("input", 4)).getWorkflowdata();
            assertThat(result.get("double").asInt()).isEqualTo(8);
            assertThat(result.get("half").asInt()).isEqualTo(2);
        }
    }

    private int duplicate(int number) {
        return number * 2;
    }

    private int half(int number) {
        return number / 2;
    }

    @Test
    void testJava() {
        final String DOUBLE = "double";
        final String HALF = "half";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("Javatest").function(java(DOUBLE, this::duplicate)).function(java(HALF, this::half))
                    .singleton(parallel()
                            .newBranch().action(call(DOUBLE, new TextNode(".input")).outputFilter(".double")).endBranch()
                            .newBranch().action(call(HALF, new TextNode(".input")).outputFilter(".half")).endBranch());

            Process<JsonNodeModel> process = application.process(workflow);
            JsonNode result = application.execute(process, Collections.singletonMap("input", 4)).getWorkflowdata();
            assertThat(result.get("double").asInt()).isEqualTo(8);
            assertThat(result.get("half").asInt()).isEqualTo(2);
        }
    }

    @Test
    void testInterpolation() {
        final String INTERPOLATION = "interpolation";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PlayingWithExpression").function(expr(INTERPOLATION, "\"My name is \\(.name)\""))
                    .singleton(operation().action(call(INTERPOLATION)));
            assertThat(application.execute(workflow, Collections.singletonMap("name", "Javierito")).getWorkflowdata().get("response").asText()).isEqualTo("My name is Javierito");
        }
    }

    @Test
    void testConstantConcatenation() {
        final String INTERPOLATION = "interpolation";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PlayingWithExpression").constant("name", "Javierito").function(expr(INTERPOLATION, "\"My name is \"+$CONST.name"))
                    .singleton(operation().action(call(INTERPOLATION)));
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata().get("response").asText()).isEqualTo("My name is Javierito");
        }
    }

    @Test
    void testConstantInterpolation() {
        final String INTERPOLATION = "interpolation";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PlayingWithExpression").constant("name", "Javierito").function(expr(INTERPOLATION, "\"My name is \\($CONST.name)\""))
                    .singleton(operation().action(call(INTERPOLATION)));
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata().get("response").asText()).isEqualTo("My name is Javierito");
        }
    }
}

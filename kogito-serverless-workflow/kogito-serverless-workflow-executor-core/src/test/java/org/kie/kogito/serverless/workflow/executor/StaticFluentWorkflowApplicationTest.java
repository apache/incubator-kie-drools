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
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.actions.WorkflowLogLevel;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;
import org.kie.kogito.serverless.workflow.utils.KogitoProcessContextResolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.serverlessworkflow.api.Workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.log;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.subprocess;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.expr;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.java;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.log;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.forEach;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.inject;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.parallel;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonArray;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class StaticFluentWorkflowApplicationTest {

    @Test
    void helloWorld() {
        final String GREETING_STRING = "Hello World!!!";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("HelloWorld").start(inject(new TextNode(GREETING_STRING))).end().build();
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).contains(new TextNode(GREETING_STRING));
        }
    }

    @Test
    void testExpr() {
        final String DOUBLE = "double";
        final String SQUARE = "square";
        final String HALF = "half";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PlayingWithExpression")
                    .start(operation().action(call(expr(DOUBLE, ".input*=2"))).action(call(expr(SQUARE, ".input*=.input"))).action(call(expr(HALF, ".input/=2"))))
                    .next(operation().action(log(WorkflowLogLevel.DEBUG, "Here we are!!!")).outputFilter("{result:.input}")).end().build();
            assertThat(application.execute(workflow, Collections.singletonMap("input", 4)).getWorkflowdata().get("result").asInt()).isEqualTo(32);
        }
    }

    @Test
    void testForEach() {
        final String SQUARE = "square";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow subflow = workflow("Square").start(operation().action(call(expr(SQUARE, ".input*.input"))).outputFilter(".response")).end().build();

            Workflow workflow = workflow("ForEachTest")
                    .start(forEach(".numbers").loopVar("input").outputCollection(".result").action(subprocess(application.process(subflow)))
                            .action(call(expr("half", "$" + ExpressionHandlerUtils.CONTEXT_MAGIC + "."
                                    + KogitoProcessContextResolver.FOR_EACH_PREV_ACTION_RESULT + "/2"))))
                    .end().build();
            assertThat(application.execute(workflow, Map.of("numbers", Arrays.asList(2, 4, 6, 8))).getWorkflowdata().get("result"))
                    .isEqualTo(jsonArray().add(2).add(8).add(18).add(32));
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
                            .when(".input%2==0").next(inject(jsonObject().put(MESSAGE, EVEN))).end()
                            .or().next(inject(jsonObject().put(MESSAGE, ODD))).end().build();
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
                    .start(parallel()
                            .newBranch().action(call(DOUBLE).outputFilter(".double")).endBranch()
                            .newBranch().action(call(HALF).outputFilter(".half")).endBranch())
                    .end().build();

            Process<JsonNodeModel> process = application.process(workflow);
            JsonNode result = application.execute(process, Collections.singletonMap("input", 4)).getWorkflowdata();
            assertThat(result.get("double").asInt()).isEqualTo(8);
            assertThat(result.get("half").asInt()).isEqualTo(2);
        }
    }

    public int duplicate(int number) {
        return number * 2;
    }

    public int half(int number) {
        return number / 2;
    }

    public int multiply(int one, int two) {
        return one * two;
    }

    @Test
    void testService() {
        final String DOUBLE = "double";
        final String PRODUCT = "product";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("ServiceTest").function(java(DOUBLE, StaticFluentWorkflowApplicationTest.class.getName(), "duplicate"))
                    .function(java(PRODUCT, StaticFluentWorkflowApplicationTest.class.getName(), "multiply"))
                    .start(parallel()
                            .newBranch().action(call(DOUBLE, ".one").outputFilter(".double")).endBranch()
                            .newBranch().action(call(PRODUCT, jsonObject().put("one", ".one").put("two", ".two")).outputFilter(".product")).endBranch())
                    .end().build();
            Process<JsonNodeModel> process = application.process(workflow);
            JsonNode result = application.execute(process, Map.of("one", 4, "two", 8)).getWorkflowdata();
            assertThat(result.get("double").asInt()).isEqualTo(8);
            assertThat(result.get("product").asInt()).isEqualTo(32);
        }
    }

    @Test
    void testJava() {
        final String DOUBLE = "double";
        final String HALF = "half";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("Javatest").function(java(DOUBLE, this::duplicate))
                    .start(parallel()
                            .newBranch().action(call(DOUBLE, new TextNode(".input")).outputFilter(".double")).endBranch()
                            .newBranch().action(call(java(HALF, this::half), new TextNode(".input")).outputFilter(".half")).endBranch())
                    .end().build();
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
                    .start(operation().action(call(INTERPOLATION))).end().build();
            assertThat(application.execute(workflow, Collections.singletonMap("name", "Javierito")).getWorkflowdata().get("response").asText()).isEqualTo("My name is Javierito");
        }
    }

    @Test
    void testConstantConcatenation() {
        final String INTERPOLATION = "interpolation";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PlayingWithExpression").constant("name", "Javierito").function(expr(INTERPOLATION, "\"My name is \"+$CONST.name"))
                    .start(operation().action(call(INTERPOLATION))).end().build();
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata().get("response").asText()).isEqualTo("My name is Javierito");
        }
    }

    @Test
    void testConstantInterpolation() {
        final String INTERPOLATION = "interpolation";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PlayingWithExpression").constant("name", "Javierito").function(expr(INTERPOLATION, "\"My name is \\($CONST.name)\""))
                    .start(operation().action(call(INTERPOLATION))).end().build();
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata().get("response").asText()).isEqualTo("My name is Javierito");
        }
    }
}

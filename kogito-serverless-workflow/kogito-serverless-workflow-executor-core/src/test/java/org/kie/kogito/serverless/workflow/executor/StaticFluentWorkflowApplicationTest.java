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
package org.kie.kogito.serverless.workflow.executor;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.actions.SysoutAction;
import org.kie.kogito.serverless.workflow.actions.WorkflowLogLevel;
import org.kie.kogito.serverless.workflow.fluent.FunctionBuilder;
import org.kie.kogito.serverless.workflow.fluent.OperationStateBuilder;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.kogito.serverless.workflow.parser.types.SysOutTypeHandler;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;
import org.kie.kogito.serverless.workflow.utils.KogitoProcessContextResolver;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
        AtomicBoolean completed = new AtomicBoolean(false);
        try (StaticWorkflowApplication application = StaticWorkflowApplication.builder().withEventListener(new DefaultKogitoProcessEventListener() {
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                completed.set(true);
            }
        }).build()) {
            Workflow workflow = workflow("HelloWorld").start(inject(new TextNode(GREETING_STRING))).end().build();
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).contains(new TextNode(GREETING_STRING));
            assertThat(completed.get()).isTrue();
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
    void testSwitchLoop() throws InterruptedException, TimeoutException {
        OperationStateBuilder startTask = operation().action(call(expr("startTask", "{finish:true}")));
        OperationStateBuilder pollTask = operation().action(call(expr("pollTask", "{finish:.finish|not}")));
        OperationStateBuilder sleepState = operation().action(call(expr("inc", ".count=.count+1")).sleepAfter(Duration.ofSeconds(1)));

        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("Polling").start(startTask)
                    .next(sleepState)
                    .next(pollTask)
                    .when(".finish").end().or().next(sleepState).end().build();
            String id = application.execute(workflow, Collections.singletonMap("count", 2)).getId();
            assertThat(application.waitForFinish(id, Duration.ofMillis(2500)).orElseThrow().getWorkflowdata().get("count").asInt()).isEqualTo(4);
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
        interpolation("\"My name is \\(.name) and my surname is \\(.surname)\"");
    }

    @Test
    void testAbreviatedInterpolation() {
        interpolation("My name is \\(.name) and my surname is \\(.surname)");
    }

    private void interpolation(String text) {
        final String INTERPOLATION = "interpolation";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PlayingWithExpression").function(expr(INTERPOLATION, text))
                    .start(operation().action(call(INTERPOLATION))).end().build();
            assertThat(application.execute(workflow, Map.of("name", "Javierito", "surname", "unknown")).getWorkflowdata().get("response").asText())
                    .isEqualTo("My name is Javierito and my surname is unknown");
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
    void testLogging() {
        Workflow workflow = workflow("Testing logs").constant("name", "Javierito")
                .start(operation()
                        .action(log(WorkflowLogLevel.INFO, "minero"))
                        .action(log(WorkflowLogLevel.INFO, "0zapatero"))
                        .action(log(WorkflowLogLevel.INFO, "keys"))
                        .action(log(WorkflowLogLevel.INFO, "\"keys:\"+({pepe:1}|keys|tostring)"))
                        .action(log(WorkflowLogLevel.INFO, "\"My name is \\($CONST.name)\""))
                        .action(log(WorkflowLogLevel.INFO, "Viva er Beti")))
                .end().build();
        assertThat(workflow.getFunctions().getFunctionDefs()).hasSize(1);
        Logger testLogger = (Logger) LoggerFactory.getLogger(SysoutAction.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        testLogger.addAppender(listAppender);
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata()).isEmpty();
            assertThat(listAppender.list).hasSize(6);
            assertThat(listAppender.list.get(0).getMessage()).isEqualTo("minero");
            assertThat(listAppender.list.get(1).getMessage()).isEqualTo("0zapatero");
            assertThat(listAppender.list.get(2).getMessage()).isEmpty();
            assertThat(listAppender.list.get(3).getMessage()).isEqualTo("keys:[\"pepe\"]");
            assertThat(listAppender.list.get(4).getMessage()).isEqualTo("My name is Javierito");
            assertThat(listAppender.list.get(5).getMessage()).isEqualTo("Viva er Beti");
        }
    }

    @Test
    void testMissingMessageException() {
        final String funcName = "badlogging";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("Testing logs").function(FunctionBuilder.def(funcName, Type.CUSTOM, SysOutTypeHandler.SYSOUT_TYPE)).start(operation().action(
                    call(funcName))).end().build();
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> application.process(workflow)).withMessageContaining("message");
        }
    }

    @Test
    void testNoArgsMessageException() {
        final String funcName = "badlogging";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("Testing logs").function(FunctionBuilder.def(funcName, Type.CUSTOM, SysOutTypeHandler.SYSOUT_TYPE)).start(operation().action(
                    call(funcName, null))).end().build();
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> application.process(workflow)).withMessageContaining("Arguments cannot be null");
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

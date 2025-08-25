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
package org.kie.kogito.serverless.workflow;

import java.util.List;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.definition.process.Process;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState.Type;
import io.serverlessworkflow.api.states.SleepState;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerlessWorkflowParsingTest extends AbstractServerlessWorkflowParsingTest {

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-operation.sw.json", "/exec/single-operation.sw.yml" })
    public void testSingleOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-operation-with-delay.sw.json", "/exec/single-operation-with-delay.sw.yml" })
    public void testSingleOperationWithDelayWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-service-operation.sw.json", "/exec/single-service-operation.sw.yml" })
    public void testSingleServiceOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-eventstate.sw.json", "/exec/single-eventstate.sw.yml" })
    public void testSingleEventStateWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-eventstate-multi-eventrefs.sw.json", "/exec/single-eventstate-multi-eventrefs.sw.yml" })
    public void testSingleEventStateMultiEventRefsWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-operation-many-functions.sw.json", "/exec/single-operation-many-functions.sw.yml" })
    public void testSingleOperationWithManyFunctionsWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/multiple-operations.sw.json", "/exec/multiple-operations.sw.yml" })
    public void testMultipleOperationWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/single-inject-state.sw.json", "/exec/single-inject-state.sw.yml" })
    public void testSingleInjectWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("function");
        assertThat(process.getName()).isEqualTo("test-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/parallel-state.sw.json", "/exec/parallel-state.sw.yml" })
    public void testParallelWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("parallelworkflow");
        assertThat(process.getName()).isEqualTo("parallel-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/transition-produce-event.sw.json", "/exec/transition-produce-event.sw.yml" })
    public void testProduceEventOnTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("produceeventontransition");
        assertThat(process.getName()).isEqualTo("Produce Event On Transition");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/eventbased-switch-state.sw.json", "/exec/eventbased-switch-state.sw.yml" })
    public void testEventBasedSwitchWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("eventswitchworkflow");
        assertThat(process.getName()).isEqualTo("event-switch-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/prchecker.sw.json", "/exec/prchecker.sw.yml" })
    public void testPrCheckerWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("prchecker");
        assertThat(process.getName()).isEqualTo("Github PR Checker Workflow");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/transition-produce-multi-events.sw.json", "/exec/transition-produce-multi-events.sw.yml" })
    public void testProduceMultiEventsOnTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("produceeventontransition");
        assertThat(process.getName()).isEqualTo("Produce Event On Transition");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-produce-events.sw.json", "/exec/switch-state-produce-events.sw.yml" })
    public void testSwitchProduceEventsOnTransitionWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("switchworkflow");
        assertThat(process.getName()).isEqualTo("switch-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = "/exec/switch-state-produce-events-default.sw.json")
    public void testSwitchProduceEventsDefaultOnTransitionWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("switchworkflow");
        assertThat(process.getName()).isEqualTo("switch-wf");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getType()).isEqualTo("SW");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/examples/applicantworkflow.sw.json", "/exec/error.sw.json", "/exec/callback.sw.json", "/exec/compensation.sw.json", "/exec/compensation.end.sw.json",
            "/exec/foreach.sw.json", "/exec/jqDelExpression.sw.json" })
    public void testSpecExamplesParsing(String workflowLocation) throws Exception {
        Workflow workflow = Workflow.fromSource(WorkflowTestUtils.readWorkflowFile(workflowLocation));

        assertThat(workflow).isNotNull();
        assertThat(workflow.getId()).isNotNull();
        assertThat(workflow.getName()).isNotNull();
        assertThat(workflow.getStates()).isNotNull().hasSizeGreaterThan(0);

        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process).isNotNull();
        assertThat(process.getId()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/expression.schema.sw.json" })
    public void testSpecWithInputSchema(String workflowLocation) throws Exception {
        Workflow workflow = Workflow.fromSource(WorkflowTestUtils.readWorkflowFile(workflowLocation));

        assertThat(workflow).isNotNull();
        assertThat(workflow.getDataInputSchema()).isNotNull();
        assertThat(workflow.getStates()).hasSizeGreaterThan(0);

        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process).isNotNull();
        assertThat(process.getId()).isNotNull();
    }

    @Test
    public void testMinimumWorkflow() {
        Workflow workflow = createMinimumWorkflow();
        ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(workflow, JavaKogitoBuildContext.builder().build());
        Process process = parser.getProcessInfo().info();
        assertThat(parser.getProcessInfo().info()).isSameAs(process);
        assertThat(process.getName()).isEqualTo(workflow.getId());
        assertThat(process.getVersion()).isEqualTo(ServerlessWorkflowParser.DEFAULT_VERSION);
        assertThat(process.getPackageName()).isEqualTo(ServerlessWorkflowParser.DEFAULT_PACKAGE);
    }

    private static Workflow createMinimumWorkflow() {
        Workflow workflow = new Workflow();
        workflow.setId("javierito");
        Start start = new Start();
        start.setStateName("javierito");
        End end = new End();
        end.setTerminate(true);
        SleepState startState = new SleepState();
        startState.setType(Type.SLEEP);
        startState.setDuration("1s");
        startState.setName("javierito");
        startState.setEnd(end);
        workflow.setStates(List.of(startState));
        workflow.setStart(start);

        return workflow;
    }

    @Test
    void testWorkflowWithAnnotations() {
        List<String> annotations = List.of("machine learning", "monitoring", "networking");

        Workflow workflow = createMinimumWorkflow().withAnnotations(annotations);

        ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(workflow, JavaKogitoBuildContext.builder().build());
        Process process = parser.getProcessInfo().info();

        assertThat(process.getMetaData()).containsEntry(Metadata.TAGS, annotations);
    }

    @Test
    void workflowWithoutAnnotationsShouldResultInProcessWithoutTags() {
        Workflow workflow = createMinimumWorkflow();

        ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(workflow, JavaKogitoBuildContext.builder().build());
        Process process = parser.getProcessInfo().info();

        assertThat(process.getMetaData()).doesNotContainKey(Metadata.TAGS);
    }

    @Test
    void testWorkflowWithDescription() {
        String description = "This is a description";

        String workflowId = "my-workflow";

        Workflow workflow = createMinimumWorkflow()
                .withId(workflowId)
                .withDescription(description);

        ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(workflow, JavaKogitoBuildContext.builder().build());
        Process process = parser.getProcessInfo().info();

        assertThat(process.getMetaData()).containsEntry(Metadata.DESCRIPTION, description);
    }
}

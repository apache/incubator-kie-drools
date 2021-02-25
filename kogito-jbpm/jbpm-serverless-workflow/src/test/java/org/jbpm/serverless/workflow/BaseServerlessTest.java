/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.serverless.workflow;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;
import org.jbpm.serverless.workflow.parser.core.ServerlessWorkflowFactory;
import org.jbpm.serverless.workflow.parser.util.WorkflowAppContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.events.EventDefinition;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.states.InjectState;
import io.serverlessworkflow.api.workflow.Events;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public abstract class BaseServerlessTest {

    protected static final Workflow singleInjectStateWorkflow = new Workflow().withStates(singletonList(
            new InjectState().withName("relayState").withType(DefaultState.Type.INJECT).withStart(new Start())
                    .withEnd(new End())));
    protected static final Workflow multiInjectStateWorkflow = new Workflow().withStates(asList(
            new InjectState().withName("relayState").withType(DefaultState.Type.INJECT).withStart(new Start())
                    .withEnd(new End()),
            new InjectState().withName("relayState2").withType(DefaultState.Type.INJECT).withEnd(new End())));
    protected static final Workflow eventDefOnlyWorkflow = new Workflow().withEvents(
            new Events(singletonList(new EventDefinition().withName("sampleEvent").withSource("sampleSource").withType("sampleType"))));
    protected static ServerlessWorkflowFactory testFactory = new ServerlessWorkflowFactory(WorkflowAppContext.ofProperties(testWorkflowProperties()));

    protected static Properties testWorkflowProperties() {
        Properties properties = new Properties();
        properties.put("kogito.sw.functions.testfunction1.testprop1", "testprop1val");
        properties.put("kogito.sw.functions.testfunction1.testprop2", "testprop2val");
        properties.put("kogito.sw.functions.testfunction2.testprop1", "testprop1val");
        properties.put("kogito.sw.functions.testfunction2.testprop2", "testprop2val");
        properties.put("kogito.sw.functions.testfunction3.ruleflowgroup", "testruleflowgroup");

        return properties;
    }

    protected ServerlessWorkflowParser getWorkflowParser(String workflowLocation) {
        ServerlessWorkflowParser parser;
        if (workflowLocation.endsWith(".sw.json")) {
            parser = new ServerlessWorkflowParser("json");
        } else {
            parser = new ServerlessWorkflowParser("yml");
        }
        return parser;
    }

    protected Reader classpathResourceReader(String location) {
        return new InputStreamReader(this.getClass().getResourceAsStream(location));
    }
}

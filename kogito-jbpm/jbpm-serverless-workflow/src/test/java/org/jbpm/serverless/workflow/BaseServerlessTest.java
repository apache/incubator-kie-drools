/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import org.jbpm.serverless.workflow.api.Workflow;
import org.jbpm.serverless.workflow.api.end.End;
import org.jbpm.serverless.workflow.api.events.EventDefinition;
import org.jbpm.serverless.workflow.api.start.Start;
import org.jbpm.serverless.workflow.api.states.DefaultState;
import org.jbpm.serverless.workflow.api.states.RelayState;
import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;
import org.jbpm.serverless.workflow.parser.core.ServerlessWorkflowFactory;
import org.jbpm.serverless.workflow.parser.util.WorkflowAppContext;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public abstract class BaseServerlessTest {

    protected static ServerlessWorkflowFactory testFactory = new ServerlessWorkflowFactory(WorkflowAppContext.ofProperties(testWorkflowProperties()));

    protected static final Workflow singleRelayStateWorkflow = new Workflow().withStates(singletonList(
            new RelayState().withName("relayState").withType(DefaultState.Type.RELAY).withStart(new Start().withKind(Start.Kind.DEFAULT))
                    .withEnd(new End(End.Kind.DEFAULT))
    ));

    protected static final Workflow multiRelayStateWorkflow = new Workflow().withStates(asList(
            new RelayState().withName("relayState").withType(DefaultState.Type.RELAY).withStart(new Start().withKind(Start.Kind.DEFAULT))
                    .withEnd(new End(End.Kind.DEFAULT)),
            new RelayState().withName("relayState2").withType(DefaultState.Type.RELAY).withEnd(new End(End.Kind.DEFAULT))
    ));

    protected static final Workflow eventDefOnlyWorkflow = new Workflow().withEvents(singletonList(
            new EventDefinition().withName("sampleEvent").withSource("sampleSource").withType("sampleType")
    ));

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

    protected static Properties testWorkflowProperties() {
        Properties properties = new Properties();
        properties.put("kogito.sw.functions.testfunction1.testprop1", "testprop1val");
        properties.put("kogito.sw.functions.testfunction1.testprop2", "testprop2val");
        properties.put("kogito.sw.functions.testfunction2.testprop1", "testprop1val");
        properties.put("kogito.sw.functions.testfunction2.testprop2", "testprop2val");
        properties.put("kogito.sw.functions.testfunction3.ruleflowgroup", "testruleflowgroup");

        return properties;
    }
}
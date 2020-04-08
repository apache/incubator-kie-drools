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

import org.jbpm.serverless.workflow.api.Workflow;
import org.jbpm.serverless.workflow.api.end.End;
import org.jbpm.serverless.workflow.api.events.EventDefinition;
import org.jbpm.serverless.workflow.api.interfaces.State;
import org.jbpm.serverless.workflow.api.start.Start;
import org.jbpm.serverless.workflow.api.states.DefaultState;
import org.jbpm.serverless.workflow.api.states.RelayState;
import org.jbpm.serverless.workflow.parser.ServerlessWorkflowParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public abstract class BaseServerlessTest {

    protected static final Workflow singleRelayStateWorkflow = new Workflow().withStates(new ArrayList<>() {{
        add(
                new RelayState().withName("relayState").withType(DefaultState.Type.RELAY).withStart(new Start().withKind(Start.Kind.DEFAULT))
                        .withEnd(new End(End.Kind.DEFAULT))
        );
    }});

    protected static final Workflow multiRelayStateWorkflow = new Workflow().withStates(new ArrayList<>() {{
        add(
                new RelayState().withName("relayState").withType(DefaultState.Type.RELAY).withStart(new Start().withKind(Start.Kind.DEFAULT))
                        .withEnd(new End(End.Kind.DEFAULT))
        );
        add(
                new RelayState().withName("relayState2").withType(DefaultState.Type.RELAY).withEnd(new End(End.Kind.DEFAULT))
        );
    }});

    protected static final Workflow eventDefOnlyWorkflow = new Workflow().withEvents(new ArrayList<>() {{
        add(
                new EventDefinition().withName("sampleEvent").withSource("sampleSource").withType("sampleType")
        );
     }});

    protected  ServerlessWorkflowParser getWorkflowParser(String workflowLocation) {
        ServerlessWorkflowParser parser;
        if(workflowLocation.endsWith(".sw.json")) {
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

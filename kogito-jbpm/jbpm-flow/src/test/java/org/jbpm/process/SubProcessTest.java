/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubProcessTest extends AbstractBaseTest {

    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testNonExistentSubProcess() {
        String nonExistentSubProcessName = "nonexistent.process";
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.process");
        process.setName("Process");
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        SubProcessNode subProcessNode = new SubProcessNode();
        subProcessNode.setName("SubProcessNode");
        subProcessNode.setId(2);
        subProcessNode.setProcessId(nonExistentSubProcessName);
        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);

        connect(startNode, subProcessNode);
        connect(subProcessNode, endNode);

        process.addNode(startNode);
        process.addNode(subProcessNode);
        process.addNode(endNode);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime(process);

        KogitoProcessInstance pi = kruntime.startProcess("org.drools.core.process.process");
        assertEquals(KogitoProcessInstance.STATE_ERROR, pi.getState());
    }

    private void connect(Node sourceNode, Node targetNode) {
        new ConnectionImpl(sourceNode, Node.CONNECTION_DEFAULT_TYPE,
                targetNode, Node.CONNECTION_DEFAULT_TYPE);
    }

}

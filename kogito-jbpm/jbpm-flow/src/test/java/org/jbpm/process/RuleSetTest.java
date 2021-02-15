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
package org.jbpm.process;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.impl.type.BooleanDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.test.Person;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleSetTest extends AbstractBaseTest {

    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testDmn() {
        String namespace = "https://kiegroup.org/dmn/_52CEF9FD-9943-4A89-96D5-6F66810CA4C1";
        String modelName = "PersonDecisions";
        String decisionName = "isAdult";

        RuleFlowProcess process = createProcess(namespace, modelName, decisionName);

        KieSession ksession = createKieSession(process);

        Map<String, Object> parameters = new HashMap<>();
        Person person = new Person("John", 25);

        parameters.put("person", person);
        parameters.put("isAdult", false);

        KogitoProcessInstance pi = (KogitoProcessInstance) ksession.startProcess("org.drools.core.process.process", parameters);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi.getState());

        boolean result = (boolean) pi.getVariables().get("isAdult");

        assertEquals(true, result);
    }

    @Test
    public void testModelNotFound() {
        String namespace = "wrong-namespace";
        String modelName = "wrong-name";
        String decisionName = "isAdult";

        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> createProcess(namespace, modelName, decisionName));
        assertTrue(illegalStateException.getMessage().contains(namespace));
        assertTrue(illegalStateException.getMessage().contains(modelName));
    }

    private RuleFlowProcess createProcess(String namespace, String modelName, String decisionName) {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(RuleSetTest.class.getResourceAsStream("/org/jbpm/process/PersonDecisions.dmn")));
        DmnDecisionModel dmnDecisionModel = new DmnDecisionModel(dmnRuntime, namespace, modelName);

        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.process");
        process.setName("Process");

        List<Variable> variables = new ArrayList<Variable>();
        Variable variable1 = new Variable();
        variable1.setName("person");
        variable1.setType(new ObjectDataType(Person.class.getName()));
        variables.add(variable1);

        Variable variable2 = new Variable();
        variable2.setName("isAdult");
        variable2.setType(new BooleanDataType());
        variables.add(variable2);
        process.getVariableScope().setVariables(variables);

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);

        RuleSetNode ruleSetNode = new RuleSetNode();
        ruleSetNode.setName("RuleSetNode");
        ruleSetNode.setId(2);
        ruleSetNode.setRuleType(RuleSetNode.RuleType.decision(namespace, modelName, decisionName));
        ruleSetNode.setLanguage(RuleSetNode.DMN_LANG);
        ruleSetNode.setDecisionModel(() -> dmnDecisionModel);
        ruleSetNode.addInMapping("Person", "person");
        ruleSetNode.addOutMapping("isAdult", "isAdult");

        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);

        connect(startNode, ruleSetNode);
        connect(ruleSetNode, endNode);

        process.addNode(startNode);
        process.addNode(ruleSetNode);
        process.addNode(endNode);
        return process;
    }

    private void connect( Node sourceNode, Node targetNode) {
        new ConnectionImpl(sourceNode, Node.CONNECTION_DEFAULT_TYPE,
                           targetNode, Node.CONNECTION_DEFAULT_TYPE);
    }
}
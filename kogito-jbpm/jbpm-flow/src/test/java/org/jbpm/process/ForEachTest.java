/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.impl.type.ListDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.test.Person;
import org.jbpm.process.test.TestProcessEventListener;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessContext;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ForEachTest extends AbstractBaseTest {
    
    public void addLogger() { 
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
    private String [] eventOrder = {
            "bvc-persons", "avc-persons",
            "bps",
            "bnt-0", "bnl-0",
            "bnt-1",
            "bvc-3:2:child", "avc-3:2:child",
            "bvc-3:2:child", "avc-3:2:child",
            "bvc-3:2:child", "avc-3:2:child",
            "bnt-1:4:8", "bnl-1:4:8", "anl-1:4:8", "ant-1:4:8",
            "bnt-1:5:12", "bnl-1:5:12", "anl-1:5:12", "ant-1:5:12",
            "bnt-1:6:15", "bnl-1:6:15",
            "bnl-1",
            "bnt-18", "bnl-18",
            "bpc", "apc",
            "anl-18", "ant-18",
            "anl-1",
            "anl-1:6:15", "ant-1:6:15",
            "ant-1",
            "anl-0", "ant-0",
            "aps"
    };
    
	@Test
    public void testForEach() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.foreach");
        process.setName("ForEach Process");
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("persons");
        ListDataType listDataType = new ListDataType();
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.jbpm.process.test.Person");
        listDataType.setType(personDataType);
        variable.setType(listDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        ForEachNode forEachNode = new ForEachNode();
        forEachNode.setName("ForEach");
        forEachNode.setId(3);
        forEachNode.setCollectionExpression("persons");
        personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        process.addNode(forEachNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            forEachNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            forEachNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        final List<String> myList = new ArrayList<String>();
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print child");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                logger.info("Executed action for child {}", ((Person) context.getVariable("child")).getName());
                myList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        forEachNode.addNode(actionNode);
        forEachNode.linkIncomingConnections(
            Node.CONNECTION_DEFAULT_TYPE,
            actionNode.getId(), Node.CONNECTION_DEFAULT_TYPE);
        forEachNode.linkOutgoingConnections(
            actionNode.getId(), Node.CONNECTION_DEFAULT_TYPE,
            Node.CONNECTION_DEFAULT_TYPE);
        forEachNode.setVariable("child", personDataType);
        
        KieSession ksession = createKieSession(process);
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        List<Person> persons = new ArrayList<Person>();
        persons.add(new Person("John Doe"));
        persons.add(new Person("Jane Doe"));
        persons.add(new Person("Jack"));
        parameters.put("persons", persons);
        
        TestProcessEventListener procEventListener = new TestProcessEventListener();
        ksession.addEventListener(procEventListener);
        ksession.startProcess("org.drools.core.process.foreach", parameters);
        assertEquals(3, myList.size());
     
        verifyEventHistory(eventOrder, procEventListener.getEventHistory());
    }
	
	
	@Test
    public void test() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("ParentProcess");
        factory.variable("x", new ObjectDataType("java.lang.String"));
        factory.variable("y", new ObjectDataType("java.lang.String"));
        factory.variable("list", new ObjectDataType("java.util.List"));
        factory.variable("listOut", new ObjectDataType("java.util.List"));
        factory.name("Parent Process");
        factory.packageName("org.drools.bpmn2");
        factory.dynamic(false);
        factory.version("1.0");
        factory.visibility("Private");
        factory.metaData("TargetNamespace", "http://www.example.org/MinimalExample");
        org.jbpm.ruleflow.core.factory.StartNodeFactory startNode1 = factory.startNode(1);
        startNode1.name("StartProcess");
        startNode1.done();
        org.jbpm.ruleflow.core.factory.ForEachNodeFactory forEachNode2 = factory.forEachNode(2);        
        forEachNode2.metaData("UniqueId", "_2");
        forEachNode2.metaData("MICollectionOutput", "_2_listOutOutput");
        forEachNode2.metaData("x", 96);
        forEachNode2.metaData("width", 110);
        forEachNode2.metaData("y", 16);
        forEachNode2.metaData("MICollectionInput", "_2_input");
        forEachNode2.metaData("height", 48);
        forEachNode2.collectionExpression("list");
        forEachNode2.variable("x", new ObjectDataType("java.lang.String"));
        forEachNode2.outputCollectionExpression("listOut");
        forEachNode2.outputVariable("y", new ObjectDataType("java.lang.String"));
        
        forEachNode2.actionNode(5).action((kcontext) -> System.out.println(kcontext.getVariable("x"))).done();
        forEachNode2.linkIncomingConnections(5);
        forEachNode2.linkOutgoingConnections(5);
        
        forEachNode2.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory endNode3 = factory.endNode(3);
        endNode3.name("EndProcess");
        endNode3.terminate(true);
        endNode3.done();
        factory.connection(1, 2, "_1-_2");
        factory.connection(2, 3, "_2-_3");
        factory.validate();
        
        List<String> list = new ArrayList<String>();
        list.add("first");
        list.add("second");
        List<String> listOut = new ArrayList<String>();
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", "oldValue");
        parameters.put("list", list);
        parameters.put("listOut", listOut);
        
        KieSession ksession = createKieSession(factory.getProcess());
        
        ksession.startProcess("ParentProcess", parameters);
    }

}

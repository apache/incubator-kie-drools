/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBaseFactory;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.datatype.impl.type.ListDataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.WorkingMemory;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ProcessContext;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.ForEachNode;
import org.drools.workflow.core.node.StartNode;

public class ForEachTest extends TestCase {
    
    public void testForEach() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.foreach");
        process.setName("ForEach Process");
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("persons");
        ListDataType listDataType = new ListDataType();
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
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
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
            	System.out.println("Executed action for child " + ((Person) context.getVariable("child")).getName());
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
        
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.addProcess(process);
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        Map<String, Object> parameters = new HashMap<String, Object>();
        List<Person> persons = new ArrayList<Person>();
        persons.add(new Person("John Doe", 30));
        persons.add(new Person("Jane Doe", 20));
        persons.add(new Person("Jack", 60));
        parameters.put("persons", persons);
        workingMemory.startProcess("org.drools.process.foreach", parameters);
        assertEquals(3, myList.size());
    }

}

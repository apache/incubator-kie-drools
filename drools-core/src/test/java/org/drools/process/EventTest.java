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
import java.util.List;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBaseFactory;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.process.instance.ProcessInstance;
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
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.StartNode;

public class EventTest extends TestCase {
    
    public void testEvent1() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.event");
        process.setName("Event Process");
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        
        MilestoneNode milestoneNode = new MilestoneNode();
        milestoneNode.setName("Milestone");
        milestoneNode.setConstraint("eval(false)");
        milestoneNode.setId(2);
        process.addNode(milestoneNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            milestoneNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        EventNode eventNode = new EventNode();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType("myEvent");
        eventNode.addEventFilter(eventFilter);
        eventNode.setVariableName("event");
        eventNode.setId(3);
        process.addNode(eventNode);
        
        final List<String> myList = new ArrayList<String>();
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
            	System.out.println("Detected event for person " + ((Person) context.getVariable("event")).getName());
                myList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        actionNode.setId(4);
        process.addNode(actionNode);
        new ConnectionImpl(
            eventNode, Node.CONNECTION_DEFAULT_TYPE,
            actionNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        Join join = new Join();
        join.setName("XOR Join");
        join.setType(Join.TYPE_XOR);
        join.setId(5);
        process.addNode(join);
        new ConnectionImpl(
            milestoneNode, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            actionNode, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
    
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(6);
        process.addNode(endNode);
        new ConnectionImpl(
            join, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.addProcess(process);
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.process.event");
        assertEquals(0, myList.size());
        Person jack = new Person();
        jack.setName("Jack");
        processInstance.signalEvent("myEvent", jack);
        assertEquals(1, myList.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testEvent2() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.event");
        process.setName("Event Process");
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        
        MilestoneNode milestoneNode = new MilestoneNode();
        milestoneNode.setName("Milestone");
        milestoneNode.setConstraint("eval(false)");
        milestoneNode.setId(2);
        process.addNode(milestoneNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            milestoneNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(3);
        process.addNode(endNode);
        new ConnectionImpl(
            milestoneNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        EventNode eventNode = new EventNode();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType("myEvent");
        eventNode.addEventFilter(eventFilter);
        eventNode.setVariableName("event");
        eventNode.setId(4);
        process.addNode(eventNode);
        
        final List<String> myList = new ArrayList<String>();
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
            	System.out.println("Detected event for person " + ((Person) context.getVariable("event")).getName());
                myList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        actionNode.setId(5);
        process.addNode(actionNode);
        new ConnectionImpl(
            eventNode, Node.CONNECTION_DEFAULT_TYPE,
            actionNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        EndNode endNode2 = new EndNode();
        endNode2.setName("EndNode");
        endNode2.setTerminate(false);
        endNode2.setId(6);
        process.addNode(endNode2);
        new ConnectionImpl(
            actionNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode2, Node.CONNECTION_DEFAULT_TYPE
        );
        
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.addProcess(process);
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.process.event");
        assertEquals(0, myList.size());
        Person jack = new Person();
        jack.setName("Jack");
        processInstance.signalEvent("myEvent", jack);
        assertEquals(1, myList.size());
        Person john = new Person();
        john.setName("John");
        processInstance.signalEvent("myEvent", john);
        assertEquals(2, myList.size());
    }

    public void testEvent3() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.event");
        process.setName("Event Process");
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        
        EventNode eventNode = new EventNode();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType("myEvent");
        eventNode.addEventFilter(eventFilter);
        eventNode.setVariableName("event");
        eventNode.setId(3);
        process.addNode(eventNode);
        
        final List<String> myList = new ArrayList<String>();
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
            	System.out.println("Detected event for person " + ((Person) context.getVariable("event")).getName());
                myList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        actionNode.setId(4);
        process.addNode(actionNode);
        new ConnectionImpl(
            eventNode, Node.CONNECTION_DEFAULT_TYPE,
            actionNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        EventNode eventNode2 = new EventNode();
        eventFilter = new EventTypeFilter();
        eventFilter.setType("myOtherEvent");
        eventNode2.addEventFilter(eventFilter);
        eventNode2.setVariableName("event");
        eventNode2.setId(5);
        process.addNode(eventNode2);
        
        ActionNode actionNode2 = new ActionNode();
        actionNode2.setName("Print");
        action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
            	System.out.println("Detected other event for person " + ((Person) context.getVariable("event")).getName());
                myList.add("Executed action");
            }
        });
        actionNode2.setAction(action);
        actionNode2.setId(6);
        process.addNode(actionNode2);
        new ConnectionImpl(
            eventNode2, Node.CONNECTION_DEFAULT_TYPE,
            actionNode2, Node.CONNECTION_DEFAULT_TYPE
        );
        
        Join join = new Join();
        join.setName("AND Join");
        join.setType(Join.TYPE_AND);
        join.setId(7);
        process.addNode(join);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            actionNode, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            actionNode2, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
    
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(8);
        process.addNode(endNode);
        new ConnectionImpl(
            join, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.addProcess(process);
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.process.event");
        assertEquals(0, myList.size());
        Person jack = new Person();
        jack.setName("Jack");
        processInstance.signalEvent("myEvent", jack);
        assertEquals(1, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processInstance.signalEvent("myEvent", jack);
        assertEquals(2, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Person john = new Person();
        john.setName("John");
        processInstance.signalEvent("myOtherEvent", john);
        assertEquals(3, myList.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testEvent4() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.event");
        process.setName("Event Process");
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        
        EventNode eventNode = new EventNode();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType("myEvent");
        eventNode.addEventFilter(eventFilter);
        eventNode.setId(3);
        process.addNode(eventNode);
        
        final List<String> myList = new ArrayList<String>();
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
                myList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        actionNode.setId(4);
        process.addNode(actionNode);
        new ConnectionImpl(
            eventNode, Node.CONNECTION_DEFAULT_TYPE,
            actionNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        EventNode eventNode2 = new EventNode();
        eventFilter = new EventTypeFilter();
        eventFilter.setType("myEvent");
        eventNode2.addEventFilter(eventFilter);
        eventNode2.setId(5);
        process.addNode(eventNode2);
        
        ActionNode actionNode2 = new ActionNode();
        actionNode2.setName("Print");
        action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
                myList.add("Executed action");
            }
        });
        actionNode2.setAction(action);
        actionNode2.setId(6);
        process.addNode(actionNode2);
        new ConnectionImpl(
            eventNode2, Node.CONNECTION_DEFAULT_TYPE,
            actionNode2, Node.CONNECTION_DEFAULT_TYPE
        );
        
        Join join = new Join();
        join.setName("AND Join");
        join.setType(Join.TYPE_AND);
        join.setId(7);
        process.addNode(join);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            actionNode, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            actionNode2, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
    
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(8);
        process.addNode(endNode);
        new ConnectionImpl(
            join, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.addProcess(process);
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.process.event");
        assertEquals(0, myList.size());
        processInstance.signalEvent("myEvent", null);
        assertEquals(2, myList.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testEvent5() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.event");
        process.setName("Event Process");
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        
        CompositeNode compositeNode = new CompositeNode();
        compositeNode.setName("CompositeNode");
        compositeNode.setId(2);
        process.addNode(compositeNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            compositeNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        MilestoneNode milestoneNode = new MilestoneNode();
        milestoneNode.setName("Milestone");
        milestoneNode.setConstraint("eval(false)");
        compositeNode.addNode(milestoneNode);
        compositeNode.linkIncomingConnections(Node.CONNECTION_DEFAULT_TYPE, milestoneNode.getId(), Node.CONNECTION_DEFAULT_TYPE);
        
        EventNode eventNode = new EventNode();
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType("myEvent");
        eventNode.addEventFilter(eventFilter);
        eventNode.setVariableName("event");
        compositeNode.addNode(eventNode);
        
        final List<String> myList = new ArrayList<String>();
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
            	System.out.println("Detected event for person " + ((Person) context.getVariable("event")).getName());
                myList.add("Executed action");
            }
        });
        actionNode.setAction(action);
        compositeNode.addNode(actionNode);
        new ConnectionImpl(
            eventNode, Node.CONNECTION_DEFAULT_TYPE,
            actionNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        Join join = new Join();
        join.setName("XOR Join");
        join.setType(Join.TYPE_XOR);
        compositeNode.addNode(join);
        new ConnectionImpl(
            milestoneNode, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            actionNode, Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE
        );
        compositeNode.linkOutgoingConnections(join.getId(), Node.CONNECTION_DEFAULT_TYPE, Node.CONNECTION_DEFAULT_TYPE);
    
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(3);
        process.addNode(endNode);
        new ConnectionImpl(
            compositeNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.addProcess(process);
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.process.event");
        assertEquals(0, myList.size());
        Person jack = new Person();
        jack.setName("Jack");
        processInstance.signalEvent("myEvent", jack);
        assertEquals(1, myList.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
}

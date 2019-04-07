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

package org.jbpm.persistence.map.impl;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.core.Work;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.persistence.session.objects.Person;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.*;

public class ProcessCreatorForHelp {

    public static RuleFlowProcess newSimpleEventProcess(String processId, String eventType) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);

        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);

        EventNode eventNode = new EventNode();
        eventNode.setName("EventNode");
        eventNode.setId(2);
        eventNode.setScope("external");
        EventTypeFilter eventFilter = new EventTypeFilter();
        eventFilter.setType(eventType);
        eventNode.addEventFilter(eventFilter);

        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);

        connect(startNode, eventNode);
        connect(eventNode, endNode);

        process.addNode(startNode);
        process.addNode(eventNode);
        process.addNode(endNode);
        return process;
    }


    public static RuleFlowProcess newProcessWithOneVariableAndOneWork(String processId, String variableName, String workName) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);

        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName(variableName);
        ObjectDataType extendingSerializableDataType = new ObjectDataType();
        extendingSerializableDataType.setClassName(Person.class.getName());
        variable.setType(extendingSerializableDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);

        StartNode startNode = new StartNode();
        startNode.setName( "Start" );
        startNode.setId(1);

        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName( "workItemNode" );
        workItemNode.setId( 2 );
        Work work = new WorkImpl();
        work.setName( workName );
        workItemNode.setWork( work );

        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(4);

        connect( startNode, workItemNode );
        connect( workItemNode, endNode );

        process.addNode( startNode );
        process.addNode( workItemNode );
        process.addNode( endNode );
        return process;
    }

    public static RuleFlowProcess newProcessWithOneWork(String processId, String workName) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);

        StartNode startNode = new StartNode();
        startNode.setName( "Start" );
        startNode.setId(1);

        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName( "workItemNode" );
        workItemNode.setId( 2 );
        Work work = new WorkImpl();
        work.setName( workName );
        workItemNode.setWork( work );

        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(4);

        connect( startNode, workItemNode );
        connect( workItemNode, endNode );

        process.addNode( startNode );
        process.addNode( workItemNode );
        process.addNode( endNode );
        return process;
    }

    public static RuleFlowProcess newProcessWithOneSubProcess(String processId, String subProcessId) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);

        StartNode startNode = new StartNode();
        startNode.setName( "Start" );
        startNode.setId(1);

        SubProcessNode subProcessNode = new SubProcessNode();
        subProcessNode.setId(2);
        subProcessNode.setProcessId(subProcessId);
        subProcessNode.setName("subProcess");

        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(4);

        connect( startNode, subProcessNode );
        connect( subProcessNode, endNode );

        process.addNode( startNode );
        process.addNode( subProcessNode );
        process.addNode( endNode );

        return process;
    }

    public static RuleFlowProcess newShortestProcess(String processId) {
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        connect(startNode, endNode);

        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(processId);
        process.addNode(startNode);
        process.addNode(endNode);
        return process;
    }

    private static void connect(Node sourceNode, Node targetNode) {
        new ConnectionImpl(sourceNode, Node.CONNECTION_DEFAULT_TYPE,
                targetNode, Node.CONNECTION_DEFAULT_TYPE);
    }

}

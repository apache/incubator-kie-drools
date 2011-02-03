package org.jbpm.persistence.map.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.persistence.map.impl.Buddy;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.core.impl.WorkImpl;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.WorkItemNode;

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
        extendingSerializableDataType.setClassName(Buddy.class.getName());
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

package org.jbpm.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.WorkItemHandlerNotFoundException;
import org.drools.common.AbstractRuleBase;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.drools.process.core.impl.WorkImpl;
import org.jbpm.JbpmTestCase;
import org.jbpm.Person;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.Assert;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

public class WorkItemTest extends JbpmTestCase {

    public void testReachNonRegisteredWorkItemHandler() {
        String processId = "org.kie.actions";
        String workName = "Unnexistent Task";
        RuleFlowProcess process = getWorkItemProcess( processId,
                                                      workName );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess( process );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put( "UserName",
                        "John Doe" );
        parameters.put( "Person",
                        new Person( "John Doe" ) );

        ProcessInstance processInstance = null;
        try {
            processInstance = ksession.startProcess( "org.kie.actions",
                                                                 parameters );
            Assert.fail( "should fail if WorkItemHandler for" + workName + "is not registered" );
        } catch ( Throwable e ) {

        }
        Assert.assertNull( processInstance );
    }

    public void testCancelNonRegisteredWorkItemHandler() {
        String processId = "org.kie.actions";
        String workName = "Unnexistent Task";
        RuleFlowProcess process = getWorkItemProcess( processId,
                                                      workName );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess( process );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.getWorkItemManager().registerWorkItemHandler( workName,
                                                               new DoNothingWorkItemHandler() );

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put( "UserName",
                        "John Doe" );
        parameters.put( "Person",
                        new Person( "John Doe" ) );

        ProcessInstance processInstance = ksession.startProcess( "org.kie.actions",
                                                                  parameters );
        long processInstanceId = processInstance.getId();
        Assert.assertEquals( ProcessInstance.STATE_ACTIVE,
                           processInstance.getState() );
        ksession.getWorkItemManager().registerWorkItemHandler( workName,
                                                               null );

        try {
            ksession.abortProcessInstance( processInstanceId );
            Assert.fail( "should fail if WorkItemHandler for" + workName + "is not registered" );
        } catch ( WorkItemHandlerNotFoundException wihnfe ) {

        }

        Assert.assertEquals( ProcessInstance.STATE_ABORTED,
                             processInstance.getState() );
    }

    private RuleFlowProcess getWorkItemProcess(String processId,
                                               String workName) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( processId );

        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName( "UserName" );
        variable.setType( new StringDataType() );
        variables.add( variable );
        variable = new Variable();
        variable.setName( "Person" );
        variable.setType( new ObjectDataType( Person.class.getName() ) );
        variables.add( variable );
        variable = new Variable();
        variable.setName( "MyObject" );
        variable.setType( new ObjectDataType() );
        variables.add( variable );
        variable = new Variable();
        variable.setName( "Number" );
        variable.setType( new IntegerDataType() );
        variables.add( variable );
        process.getVariableScope().setVariables( variables );

        StartNode startNode = new StartNode();
        startNode.setName( "Start" );
        startNode.setId( 1 );

        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName( "workItemNode" );
        workItemNode.setId( 2 );
        workItemNode.addInMapping( "Comment",
                                   "Person.name" );
        workItemNode.addInMapping( "Attachment",
                                   "MyObject" );
        workItemNode.addOutMapping( "Result",
                                    "MyObject" );
        workItemNode.addOutMapping( "Result.length()",
                                    "Number" );
        Work work = new WorkImpl();
        work.setName( workName );
        Set<ParameterDefinition> parameterDefinitions = new HashSet<ParameterDefinition>();
        ParameterDefinition parameterDefinition = new ParameterDefinitionImpl( "ActorId",
                                                                               new StringDataType() );
        parameterDefinitions.add( parameterDefinition );
        parameterDefinition = new ParameterDefinitionImpl( "Content",
                                                           new StringDataType() );
        parameterDefinitions.add( parameterDefinition );
        parameterDefinition = new ParameterDefinitionImpl( "Comment",
                                                           new StringDataType() );
        parameterDefinitions.add( parameterDefinition );
        work.setParameterDefinitions( parameterDefinitions );
        work.setParameter( "ActorId",
                           "#{UserName}" );
        work.setParameter( "Content",
                           "#{Person.name}" );
        workItemNode.setWork( work );

        EndNode endNode = new EndNode();
        endNode.setName( "End" );
        endNode.setId( 3 );

        connect( startNode,
                 workItemNode );
        connect( workItemNode,
                 endNode );

        process.addNode( startNode );
        process.addNode( workItemNode );
        process.addNode( endNode );

        return process;
    }

    private void connect(Node sourceNode,
                         Node targetNode) {
        new ConnectionImpl( sourceNode,
                             Node.CONNECTION_DEFAULT_TYPE,
                             targetNode,
                             Node.CONNECTION_DEFAULT_TYPE );
    }

}

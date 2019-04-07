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

package org.jbpm.persistence.session;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.jbpm.persistence.session.objects.Person;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.impl.type.IntegerDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.*;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

@RunWith(Parameterized.class)
public class WorkItemPersistenceTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(WorkItemPersistenceTest.class);
    
    private HashMap<String, Object> context;
    private EntityManagerFactory emf;
    
    static {
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
    }
    
    public WorkItemPersistenceTest(boolean locking) { 
        this.useLocking = locking; 
     }
     
     @Parameters
     public static Collection<Object[]> persistence() {
         Object[][] data = new Object[][] { { false }, { true } };
         return Arrays.asList(data);
     };
     
    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
    }
    
    @After
    public void tearDown() throws Exception {
       cleanUp(context); 
    }
   
    protected KieSession createSession(KieBase kbase) {
        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, createEnvironment(context) );
    }

    @Test
    @Ignore
    public void testCancelNonRegisteredWorkItemHandler() {
        String processId = "org.drools.actions";
        String workName = "Unnexistent Task";
        RuleFlowProcess process = getWorkItemProcess( processId, workName );
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((KnowledgeBaseImpl) kbase).addProcess( process );
        KieSession ksession = createSession(kbase);

        ksession.getWorkItemManager().registerWorkItemHandler( workName, new DoNothingWorkItemHandler() );

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put( "UserName", "John Doe" );
        parameters.put( "Person",
                        new Person( "John Doe" ) );

        ProcessInstance processInstance = ksession.startProcess( "org.drools.actions",
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

        Assert.assertEquals( ProcessInstance.STATE_ABORTED, processInstance.getState() );
    }

    private RuleFlowProcess getWorkItemProcess(String processId, String workName) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( processId );

        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName( "UserName" );
        variable.setType( new StringDataType() );
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

        HumanTaskNode workItemNode = new HumanTaskNode();
        workItemNode.setName( "workItemNode" );
        workItemNode.setId( 2 );
        workItemNode.addInMapping( "Attachment", "MyObject" );
        workItemNode.addOutMapping( "Result", "MyObject" );
        workItemNode.addOutMapping( "Result.length()", "Number" );
        
        Work work = new WorkImpl();
        work.setName( workName );
        
        Set<ParameterDefinition> parameterDefinitions = new HashSet<ParameterDefinition>();
        ParameterDefinition parameterDefinition = new ParameterDefinitionImpl( "ActorId", new StringDataType() );
        parameterDefinitions.add( parameterDefinition );
        parameterDefinition = new ParameterDefinitionImpl( "Content", new StringDataType() );
        parameterDefinitions.add( parameterDefinition );
        parameterDefinition = new ParameterDefinitionImpl( "Comment", new StringDataType() );
        parameterDefinitions.add( parameterDefinition );
        work.setParameterDefinitions( parameterDefinitions );
        
        work.setParameter( "ActorId", "#{UserName}" );
        work.setParameter( "Content", "#{Person.name}" );
        workItemNode.setWork( work );

        EndNode endNode = new EndNode();
        endNode.setName( "End" );
        endNode.setId( 3 );

        connect( startNode, workItemNode );
        connect( workItemNode, endNode );

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

    @Test
    public void testHumanTask() {
        List<ProcessInstanceInfo> procInstInfoList = retrieveProcessInstanceInfo(emf);
        int numProcInstInfos = procInstInfoList.size();
        
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.humantask\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <humanTask id=\"2\" name=\"HumanTask\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "        <parameter name=\"ActorId\" >\n" +
            "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>John Doe</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"TaskName\" >\n" +
            "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>Do something</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Priority\" >\n" +
            "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Comment\" >\n" +
            "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "      </work>\n" +
            "    </humanTask>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource(source), ResourceType.DRF );
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );        
        KieSession ksession = createSession(kbase);
        final List<WorkItem> workItems = new ArrayList<WorkItem>();
        DoNothingWorkItemHandler handler = new DoNothingWorkItemHandler() {

            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                super.executeWorkItem(workItem, manager);
                workItems.add(workItem);
            }
            
        };
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        ProcessInstance processInstance = ksession.startProcess("org.drools.humantask");
        
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        int state = processInstance.getState();
        switch(state) { 
        case ProcessInstance.STATE_ABORTED:
            logger.debug("STATE_ABORTED");
            break;
        case ProcessInstance.STATE_ACTIVE:
            logger.debug("STATE_ACTIVE");
            break;
        case ProcessInstance.STATE_COMPLETED:
            logger.debug("STATE_COMPLETED");
            break;
        case ProcessInstance.STATE_PENDING:
            logger.debug("STATE_PENDING");
            break;
        case ProcessInstance.STATE_SUSPENDED:
            logger.debug("STATE_SUSPENDED");
            break;
        default: 
            logger.debug("Unknown state: {}", state );
        }
       
        procInstInfoList = retrieveProcessInstanceInfo(emf);
        assertTrue( (procInstInfoList.size() - numProcInstInfos) == 1);
        
        ProcessInstanceInfo processInstanceInfoMadeInThisTest = procInstInfoList.get(numProcInstInfos);
        assertNotNull("ByteArray of ProcessInstanceInfo from this test is not filled and null!", 
                processInstanceInfoMadeInThisTest.getProcessInstanceByteArray());
        assertTrue("ByteArray of ProcessInstanceInfo from this test is not filled and empty!", 
                processInstanceInfoMadeInThisTest.getProcessInstanceByteArray().length > 0);
        assertEquals(1, workItems.size());
        ksession.getWorkItemManager().completeWorkItem(workItems.get(0).getId(), null);
        
        ProcessInstance pi = ksession.getProcessInstance(processInstance.getId());
        assertNull(pi);
    }
    
    @SuppressWarnings("unchecked")
    public static ArrayList<ProcessInstanceInfo> retrieveProcessInstanceInfo(EntityManagerFactory emf) { 
        
        JtaTransactionManager txm = new JtaTransactionManager(null, null, null);
        boolean txOwner = txm.begin();
    
        EntityManager em = emf.createEntityManager();
        
        ArrayList<ProcessInstanceInfo> procInstInfoList = new ArrayList<ProcessInstanceInfo>();
        List<Object> mdList = em.createQuery("SELECT p FROM ProcessInstanceInfo p").getResultList();
        for( Object resultObject : mdList ) { 
            ProcessInstanceInfo procInstInfo = (ProcessInstanceInfo) resultObject;
            procInstInfoList.add(procInstInfo);
            logger.trace("> {}", procInstInfo);
        }
        
        txm.commit(txOwner);
        
        return procInstInfoList;
    }
    
}

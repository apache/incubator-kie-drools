package org.jbpm.persistence.session;

import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.WorkItemHandlerNotFoundException;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractRuleBase;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.util.MarshallingTestUtil;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.drools.process.core.impl.WorkImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.jbpm.persistence.session.objects.Person;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkItemPersistenceTest {

    private static Logger logger = LoggerFactory.getLogger(WorkItemPersistenceTest.class);
    
    private HashMap<String, Object> context;
    private EntityManagerFactory emf;
    
    static {
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
    }
    
    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME, false);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
    }
    
    @After
    public void tearDown() throws Exception {
       PersistenceUtil.tearDown(context); 
    }
   
    @AfterClass
    public static void compareMarshalledData() { 
        MarshallingTestUtil.compareMarshallingDataFromTest(JBPM_PERSISTENCE_UNIT_NAME);
    }

    protected StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, createEnvironment(context) );
    }

    @Test
    @Ignore
    public void testCancelNonRegisteredWorkItemHandler() {
        String processId = "org.drools.actions";
        String workName = "Unnexistent Task";
        RuleFlowProcess process = getWorkItemProcess( processId, workName );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess( process );
        StatefulKnowledgeSession ksession = createSession(kbase);

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
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>John Doe</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"TaskName\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>Do something</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Priority\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Comment\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
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
        kbuilder.add( ResourceFactory.newReaderResource( source ), ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );        
        StatefulKnowledgeSession ksession = createSession(kbase);
        
        DoNothingWorkItemHandler handler = new DoNothingWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        ProcessInstance processInstance = ksession.startProcess("org.drools.humantask");
        
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        int state = processInstance.getState();
        switch(state) { 
        case ProcessInstance.STATE_ABORTED:
            logger.info("STATE_ABORTED");
            break;
        case ProcessInstance.STATE_ACTIVE:
            logger.info("STATE_ACTIVE");
            break;
        case ProcessInstance.STATE_COMPLETED:
            logger.info("STATE_COMPLETED");
            break;
        case ProcessInstance.STATE_PENDING:
            logger.info("STATE_PENDING");
            break;
        case ProcessInstance.STATE_SUSPENDED:
            logger.info("STATE_SUSPENDED");
            break;
        default: 
            logger.info("Unknown state: " + state );
        }
       
        procInstInfoList = retrieveProcessInstanceInfo(emf);
        assertTrue( (procInstInfoList.size() - numProcInstInfos) == 1);
        
        ProcessInstanceInfo processInstanceInfoMadeInThisTest = procInstInfoList.get(numProcInstInfos);
        assertNotNull("ByteArray of ProcessInstanceInfo from this test is not filled and null!", 
                processInstanceInfoMadeInThisTest.getProcessInstanceByteArray());
        assertTrue("ByteArray of ProcessInstanceInfo from this test is not filled and empty!", 
                processInstanceInfoMadeInThisTest.getProcessInstanceByteArray().length > 0);
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
            logger.trace("> " + procInstInfo);
        }
        
        txm.commit(txOwner);
        
        return procInstInfoList;
    }
    
}

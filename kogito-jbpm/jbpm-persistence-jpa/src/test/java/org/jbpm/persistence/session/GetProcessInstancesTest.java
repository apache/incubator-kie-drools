package org.jbpm.persistence.session;

import static org.jbpm.persistence.util.PersistenceUtil.*;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.StatefulKnowledgeSession;
import org.jbpm.persistence.JbpmTestCase;
import org.jbpm.persistence.processinstance.JPAProcessInstanceManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test looks at the behavior of the  {@link JPAProcessInstanceManager} 
 * with regards to created (but not started) process instances 
 * and whether the process instances are available or not after creation.
 */
public class GetProcessInstancesTest extends JbpmTestCase {
    
    private HashMap<String, Object> context;
    
    private Environment env;
    private KnowledgeBase kbase;
    private int sessionId;

    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);

        kbase = createBase();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        sessionId = ksession.getId();
        ksession.dispose();
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    @Test
    public void getEmptyProcessInstances() throws Exception {
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        assertEquals(0, ksession.getProcessInstances().size());
        ksession.dispose();
    }

    @Test
    public void create2ProcessInstances() throws Exception {
        long[] processId = new long[2];

        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        processId[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        processId[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        ksession.dispose();

        assertProcessInstancesExist(processId);
    }

    @Test
    public void create2ProcessInstancesInsideTransaction() throws Exception {
        long[] processId = new long[2];

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        processId[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        processId[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        assertEquals(2, ksession.getProcessInstances().size());
        
        // process instance manager cache flushed on tx
        ut.commit();
        assertEquals(0, ksession.getProcessInstances().size());

        ksession = reloadKnowledgeSession(ksession);
        assertEquals(0, ksession.getProcessInstances().size());
        ksession.dispose();
        
        assertProcessInstancesExist(processId);
    }

    @Test
    public void noProcessInstancesLeftAfterRollback() throws Exception {
        long[] notProcess = new long[2];

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        notProcess[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        notProcess[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        assertEquals(2, ksession.getProcessInstances().size());
        
        ut.rollback();
        // Validate that proc inst mgr cache is also flushed on rollback
        assertEquals(0, ksession.getProcessInstances().size());

        ksession = reloadKnowledgeSession(ksession);
        assertEquals(0, ksession.getProcessInstances().size());
        ksession.dispose();
        
        assertProcessInstancesNotExist(notProcess);
    }

    @Test
    public void noProcessInstancesLeftWithPreTxKSessionAndRollback() throws Exception {
        long[] notProcess = new long[4];

        StatefulKnowledgeSession ksession = reloadKnowledgeSession();
        
        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        
        notProcess[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        notProcess[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        
        ut.rollback();
        // Validate that proc inst mgr cache is also flushed on rollback
        assertEquals(0, ksession.getProcessInstances().size());

        ksession = reloadKnowledgeSession(ksession);
        assertEquals(0, ksession.getProcessInstances().size());
        ksession.dispose();
        
        assertProcessInstancesNotExist(notProcess);
    }

   
    /**
     * Helper functions
     */
    
    private void assertProcessInstancesExist(long[] processId) {
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();

        for (long id : processId) {
            assertNotNull("Process instance " + id + " should not exist!", ksession.getProcessInstance(id));
        }
    }

    private void assertProcessInstancesNotExist(long[] processId) {
        StatefulKnowledgeSession ksession = reloadKnowledgeSession();

        for (long id : processId) {
            assertNull(ksession.getProcessInstance(id));
        }
    }

    private KnowledgeBase createBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/HelloWorld.rf"), ResourceType.DRF);
        assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());

        return kbuilder.newKnowledgeBase();
    }
    
    private StatefulKnowledgeSession reloadKnowledgeSession() {
        return JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
    }

    private StatefulKnowledgeSession reloadKnowledgeSession(StatefulKnowledgeSession ksession) {
        ksession.dispose();
        return reloadKnowledgeSession();
    }
}

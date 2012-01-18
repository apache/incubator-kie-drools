package org.jbpm.persistence.session;

import static org.drools.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.persistence.JbpmTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetProcessInstancesTest extends JbpmTestCase {
    private HashMap<String, Object> context;
    private Environment env;
    private KnowledgeBase kbase;
    private int sessionId;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        env = PersistenceUtil.createEnvironment(context);

        kbase = createBase();
        sessionId = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env).getId();
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.tearDown(context);
    }

    @Test
    public void testGetProcessInstances0() throws Exception {
        StatefulKnowledgeSession ksession = getSession();
        Assert.assertEquals(0, ksession.getProcessInstances());

    }

    @Test
    public void testGetProcessInstances1() throws Exception {
        long[] processId = new long[2];

        StatefulKnowledgeSession ksession = getSession();
        processId[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        processId[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();

        assertProcessInstancesExist(processId);
    }

    @Test
    public void testGetProcessInstances2() throws Exception {
        long[] processId = new long[2];

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        StatefulKnowledgeSession ksession = getSession();
        processId[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        processId[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        ut.commit();

        assertProcessInstancesExist(processId);
    }

    @Test
    public void testGetProcessInstances3() throws Exception {
        long[] notProcess = new long[2];

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        StatefulKnowledgeSession ksession = getSession();
        notProcess[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        notProcess[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        ut.rollback();

        ksession = getSession();
        Assert.assertEquals(ksession.getProcessInstances().size(), 0);

        assertProcessInstancesNotExist(notProcess);
    }

    @Test
    public void testGetProcessInstances4() throws Exception {
        long[] notProcess = new long[4];

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        StatefulKnowledgeSession ksession = getSession();
        ut.begin();
        notProcess[0] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        notProcess[1] = ksession.createProcessInstance("org.jbpm.processinstance.helloworld", null).getId();
        ut.rollback();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
        Assert.assertEquals(ksession.getProcessInstances().size(), 0);

        assertProcessInstancesNotExist(notProcess);
    }

    private void assertProcessInstancesExist(long[] processId) {
        StatefulKnowledgeSession ksession = getSession();

        Assert.assertEquals(processId.length, ksession.getProcessInstances().size());
        for (long id : processId) {
            Assert.assertNotNull(ksession.getProcessInstance(id));
        }
    }

    private void assertProcessInstancesNotExist(long[] processId) {
        StatefulKnowledgeSession ksession = getSession();

        for (long id : processId) {
            Assert.assertNull(ksession.getProcessInstance(id));
        }
    }

    private KnowledgeBase createBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/HelloWorld.rf"), ResourceType.DRF);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());

        return kbuilder.newKnowledgeBase();
    }

    private StatefulKnowledgeSession getSession() {
        return JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
    }
}

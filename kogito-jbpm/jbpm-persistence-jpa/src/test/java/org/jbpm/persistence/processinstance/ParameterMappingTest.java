package org.jbpm.persistence.processinstance;

import static org.drools.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.drools.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.drools.persistence.util.PersistenceUtil.tearDown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.persistence.JbpmTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ParameterMappingTest extends JbpmTestCase {
    private static final String PROCESS_ID = "org.jbpm.processinstance.subprocess";
    private static final String SUBPROCESS_ID = "org.jbpm.processinstance.helloworld";
    private StatefulKnowledgeSession ksession;
    private HashMap<String, Object> context;
    private ProcessListener listener;

    @Before
    public void before() {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME, false);
        Environment env = createEnvironment(context);

        ksession = JPAKnowledgeService.newStatefulKnowledgeSession(createKnowledgeBase(), null, env);
        Assert.assertTrue("Valid KnowledgeSession could not be created.", ksession != null && ksession.getId() > 0);

        listener = new ProcessListener();
        ksession.addEventListener(listener);
    }

    private KnowledgeBase createKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/Subprocess.rf"), ResourceType.DRF);
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/HelloWorld.rf"), ResourceType.DRF);

        return kbuilder.newKnowledgeBase();
    }

    //org.jbpm.processinstance.subprocess
    @Test
    public void testChangingVariableByScript() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "script");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping).getId();

        Thread.sleep(500);

        Assert.assertTrue(listener.isProcessStarted(PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEvent() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");

        ProcessInstance pi = ksession.startProcess(PROCESS_ID, mapping);
        pi.signalEvent("pass", "new value");

        Thread.sleep(500);

        Assert.assertTrue(listener.isProcessStarted(PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEvent2() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");

        long processId = ksession.startProcess(PROCESS_ID, mapping).getId();
        ksession.getProcessInstance(processId).signalEvent("pass", "new value");

        Thread.sleep(500);

        Assert.assertTrue(listener.isProcessStarted(PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEvent3() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping).getId();
        ksession.signalEvent("pass", "new value");

        Thread.sleep(500);

        Assert.assertTrue(listener.isProcessStarted(PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEvent4() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");

        long processId = ksession.startProcess(PROCESS_ID, mapping).getId();
        ksession.signalEvent("pass", "new value", processId);

        Thread.sleep(500);

        Assert.assertTrue(listener.isProcessStarted(PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testNotChangingVariable() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "default");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping);

        Thread.sleep(500);

        Assert.assertTrue(listener.isProcessStarted(PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testNotSettingVariable() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "default");

        ksession.startProcess(PROCESS_ID, mapping);

        Thread.sleep(500);

        Assert.assertTrue(listener.isProcessStarted(PROCESS_ID));
        Assert.assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        Assert.assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @After
    public void after() {
        if (ksession != null) {
            ksession.dispose();
        }
        tearDown(context);
    }

    public static class ProcessListener extends DefaultProcessEventListener {
        private final List<String> processesStarted = new ArrayList<String>();
        private final List<String> processesCompleted = new ArrayList<String>();

        public void afterProcessStarted(ProcessStartedEvent event) {
            processesStarted.add(event.getProcessInstance().getProcessId());
        }

        public void afterProcessCompleted(ProcessCompletedEvent event) {
            processesCompleted.add(event.getProcessInstance().getProcessId());
        }

        public boolean isProcessStarted(String processId) {
            return processesStarted.contains(processId);
        }

        public boolean isProcessCompleted(String processId) {
            return processesCompleted.contains(processId);
        }
    }
}

package org.jbpm.persistence.processinstance;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.persistence.JbpmTestCase;
import org.jbpm.persistence.util.LoggingPrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.event.process.DefaultProcessEventListener;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.io.ResourceFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.StatefulKnowledgeSession;

/**
 * This test 
 * 
 *
 */
public class ParameterMappingTest extends JbpmTestCase {
    
    private HashMap<String, Object> context;
    
    private static final String PROCESS_ID = "org.jbpm.processinstance.subprocess";
    private static final String SUBPROCESS_ID = "org.jbpm.processinstance.helloworld";
    private StatefulKnowledgeSession ksession;
    private ProcessListener listener;

    // Want to see the System.out output? Set the debug level for console in log4j.xml to DEBUG.
    static { 
        System.setOut(new LoggingPrintStream(System.out));
    }
    
    @Before
    public void before() {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME, false);
        Environment env = createEnvironment(context);

        ksession = JPAKnowledgeService.newStatefulKnowledgeSession(createKnowledgeBase(), null, env);
        assertTrue("Valid KnowledgeSession could not be created.", ksession != null && ksession.getId() > 0);

        listener = new ProcessListener();
        ksession.addEventListener(listener);
    }

    private KnowledgeBase createKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/Subprocess.rf"), ResourceType.DRF);
        kbuilder.add(ResourceFactory.newClassPathResource("processinstance/HelloWorld.rf"), ResourceType.DRF);
    
        return kbuilder.newKnowledgeBase();
    }

    @After
    public void after() {
        if (ksession != null) {
            ksession.dispose();
        }
        cleanUp(context);
    }

    //org.jbpm.processinstance.subprocess
    @Test
    public void testChangingVariableByScript() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "script");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping);

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEvent() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping).getId();
        ksession.signalEvent("pass", "new value");

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testChangingVariableByEventSignalWithProcessId() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "event");
        mapping.put("var", "value");

        long processId = ksession.startProcess(PROCESS_ID, mapping).getId();
        ksession.signalEvent("pass", "new value", processId);

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testNotChangingVariable() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "default");
        mapping.put("var", "value");

        ksession.startProcess(PROCESS_ID, mapping);

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
    }

    @Test
    public void testNotSettingVariable() throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("type", "default");

        ksession.startProcess(PROCESS_ID, mapping);

        assertTrue(listener.isProcessStarted(PROCESS_ID));
        assertTrue(listener.isProcessStarted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(SUBPROCESS_ID));
        assertTrue(listener.isProcessCompleted(PROCESS_ID));
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

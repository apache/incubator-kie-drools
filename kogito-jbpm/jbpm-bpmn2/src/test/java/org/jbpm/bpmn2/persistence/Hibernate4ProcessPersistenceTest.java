package org.jbpm.bpmn2.persistence;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.PackageBuilderConfiguration;
import org.jbpm.bpmn2.JbpmBpmn2TestCase.TestWorkItemHandler;
import org.jbpm.bpmn2.SimpleBPMNProcessTest;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.definition.process.Process;
import org.kie.io.ResourceFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hibernate4ProcessPersistenceTest {

    Logger logger = LoggerFactory.getLogger(Hibernate4ProcessPersistenceTest.class);
    HashMap<String, Object> context = new HashMap<String, Object>();
    
    @Before
    public void setUp() {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
    }
    
    @After
    public void tearDown() { 
        cleanUp(context);
    }

   // Helper methods at the end. 
    
    @Test
    public void testLane() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-Lane.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("ActorId", "mary");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(),
                results);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("mary", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    @Test
    public void testAdHocSubProcessAutoComplete() throws Exception {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
                .newKnowledgeBuilderConfiguration();
        ((PackageBuilderConfiguration) conf).initSemanticModules();
        ((PackageBuilderConfiguration) conf)
                .addSemanticModule(new BPMNSemanticModule());
        ((PackageBuilderConfiguration) conf)
                .addSemanticModule(new BPMNDISemanticModule());
        // ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
        XmlProcessReader processReader = new XmlProcessReader(
                ((PackageBuilderConfiguration) conf).getSemanticModules(),
                getClass().getClassLoader());
        List<Process> processes = processReader
                .read(SimpleBPMNProcessTest.class
                        .getResourceAsStream("/BPMN2-AdHocSubProcessAutoComplete.bpmn2"));
        assertNotNull(processes);
        assertEquals(1, processes.size());
        RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder(conf);
        // logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
                XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory
                .newClassPathResource("BPMN2-AdHocSubProcess.drl"),
                ResourceType.DRL);
        if (!kbuilder.getErrors().isEmpty()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                logger.error(error.toString());
            }
            throw new IllegalArgumentException(
                    "Errors while parsing knowledge base");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("AdHocSubProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNull(workItem);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ksession.fireAllRules();
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    protected KnowledgeBase createKnowledgeBase(String... process) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String p: process) {
            kbuilder.add(ResourceFactory.newClassPathResource(p), ResourceType.BPMN2);
        }
        return kbuilder.newKnowledgeBase();
    }

    public void assertProcessInstanceCompleted(long processInstanceId, StatefulKnowledgeSession ksession) {
        assertNull(ksession.getProcessInstance(processInstanceId));
    }

    public void assertNodeTriggered(long processInstanceId, String... nodeNames) {
        List<String> names = new ArrayList<String>();
        for (String nodeName: nodeNames) {
            names.add(nodeName);
        }
        List<NodeInstanceLog> logs = JPAProcessInstanceDbLog.findNodeInstances(processInstanceId);
        if (logs != null) {
            for (NodeInstanceLog l: logs) {
                String nodeName = l.getNodeName();
                if ((l.getType() == NodeInstanceLog.TYPE_ENTER || l.getType() == NodeInstanceLog.TYPE_EXIT) && names.contains(nodeName)) {
                    names.remove(nodeName);
                }
            }
        }
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) not executed: " + s);
        }
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
        Environment env = createEnvironment(context);
    
        StatefulKnowledgeSession result = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        new JPAWorkingMemoryDbLogger(result);
        JPAProcessInstanceDbLog.setEnvironment(result.getEnvironment());
        return result;
    }

    protected StatefulKnowledgeSession restoreSession(StatefulKnowledgeSession ksession, boolean noCache) {
        int id = ksession.getId();
        KnowledgeBase kbase = ksession.getKnowledgeBase();
        Environment env = null;
        env = createEnvironment(context);
        KnowledgeSessionConfiguration config = ksession.getSessionConfiguration();
        StatefulKnowledgeSession result = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, config, env);
        new JPAWorkingMemoryDbLogger(result);
        return result;
    }

    public void assertProcessInstanceActive(long processInstanceId, StatefulKnowledgeSession ksession) {
        assertNotNull(ksession.getProcessInstance(processInstanceId));
    }
}

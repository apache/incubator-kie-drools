//package org.droolsjbpm.services.test;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import javax.inject.Inject;
//
//import org.droolsjbpm.services.api.KnowledgeAdminDataService;
//import org.droolsjbpm.services.api.RuntimeDataService;
//import org.droolsjbpm.services.api.KnowledgeDomainService;
//import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
//import org.droolsjbpm.services.impl.SimpleDomainImpl;
//import org.droolsjbpm.services.impl.model.NodeInstanceDesc;
//import org.droolsjbpm.services.impl.model.ProcessDesc;
//import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
//import org.jbpm.shared.services.api.Domain;
//import org.jbpm.shared.services.api.FileService;
//import org.jbpm.shared.services.api.ServicesSessionManager;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.kie.api.runtime.KieSession;
//import org.kie.api.runtime.process.ProcessInstance;
//import org.kie.internal.task.api.TaskService;
//
//
//public abstract class KnowledgeDataServiceBaseTest {
//
//    @Inject
//    protected transient TaskService taskService;
//    @Inject
//    private BPMN2DataService bpmn2Service;
//    @Inject
//    protected RuntimeDataService dataService;
//    @Inject
//    protected KnowledgeAdminDataService adminDataService;
//    @Inject
//    private FileService fs;
//    @Inject
//    private ServicesSessionManager sessionManager;
//    @Inject
//    private KnowledgeDomainService domainService;
//    
//    @Before
//    public void setup() {
//        Domain myDomain = new SimpleDomainImpl("myDomain");
//        sessionManager.setDomain(myDomain);
//        sessionManager.buildSession("myKsession", "processes/general/", true);
//    }
//    
//    @After
//    public void teardown() {
//        
//    }
//    
//    @Test
//    public void testGetProcessesByFilter() {
//        assertNotNull(dataService);
//        
//        // find by complete id
//        Collection<ProcessDesc> foundProcesses = dataService.getProcessesByFilter("signal");
//        
//        assertNotNull(foundProcesses);
//        assertEquals(1, foundProcesses.size());
//        
//        // find by partial id
//        foundProcesses = dataService.getProcessesByFilter("sign");
//        
//        assertNotNull(foundProcesses);
//        assertEquals(1, foundProcesses.size());
//        
//        // find by complete name
//        foundProcesses = dataService.getProcessesByFilter("Default Process");
//        
//        assertNotNull(foundProcesses);
//        assertEquals(1, foundProcesses.size());
//        
//        // find by complete name
//        foundProcesses = dataService.getProcessesByFilter("Default ");
//        
//        assertNotNull(foundProcesses);
//        assertEquals(1, foundProcesses.size());
//    }
//    
//    @Test
//    public void testGetProcessInstancesByFilter() {
//        assertNotNull(dataService);
//        List<Integer> states = new ArrayList<Integer>();
//        states.add(ProcessInstance.STATE_ACTIVE);
//        
//        // search on empty db
//        Collection<ProcessInstanceDesc> foundInstances = dataService.getProcessInstances(states, null);
//        
//        assertNotNull(foundInstances);
//        assertEquals(0, foundInstances.size());
//        
//        KieSession ksession = sessionManager.getKsessionById(1);
//        ksession.startProcess("signal");
//        
//        // find process just by state
//        foundInstances = dataService.getProcessInstances(states, null);
//        
//        assertNotNull(foundInstances);
//        assertEquals(1, foundInstances.size()); 
//        
//        // find process instances by complete process id
//        foundInstances = dataService.getProcessInstancesByProcessId(states, "signal", null);
//        
//        assertNotNull(foundInstances);
//        assertEquals(1, foundInstances.size());
//        
//        // find process instances by partial process id
//        foundInstances = dataService.getProcessInstancesByProcessId(states, "sig", null);
//        
//        assertNotNull(foundInstances);
//        assertEquals(1, foundInstances.size());
//        
//        // find process instances by complete process name
//        foundInstances = dataService.getProcessInstancesByProcessName(states, "Default Process", null);
//        
//        assertNotNull(foundInstances);
//        assertEquals(1, foundInstances.size());
//        
//        // find process instances by partial process name
//        foundInstances = dataService.getProcessInstancesByProcessName(states, "Default ", null);
//        
//        assertNotNull(foundInstances);
//        assertEquals(1, foundInstances.size());
//    }
//    
//    @Test
//    public void testGetCompletedNodes() {
//        
//        KieSession ksession = sessionManager.getKsessionById(1);
//        ProcessInstance pi = ksession.startProcess("signal");
//        
//        List<NodeInstanceDesc> executedNodes = new ArrayList<NodeInstanceDesc>(dataService.getProcessInstanceCompletedNodes(1, pi.getId()));
//        assertNotNull(executedNodes);
//        assertEquals(2, executedNodes.size());
//        NodeInstanceDesc startNode = executedNodes.get(0);
//        
//        assertNotNull(startNode);
//        assertEquals("StartProcess", startNode.getName());
//        assertEquals("StartEvent_1", startNode.getNodeId());
//        assertEquals(null, startNode.getConnection());
//        
//        startNode = executedNodes.get(1);
//        
//        assertNotNull(startNode);
//        assertEquals("StartProcess", startNode.getName());
//        assertEquals("StartEvent_1", startNode.getNodeId());
//        assertNotNull(startNode.getConnection());
//        assertEquals("StartEvent_1-IntermediateCatchEvent_1", startNode.getConnection());
//        
//        ksession.signalEvent("MySignal", null, pi.getId());
//        
//        executedNodes = new ArrayList<NodeInstanceDesc>(dataService.getProcessInstanceCompletedNodes(1, pi.getId()));
//        assertNotNull(executedNodes);
//        assertEquals(8, executedNodes.size());
//        
//        List<NodeInstanceDesc> completed = new ArrayList<NodeInstanceDesc>(executedNodes);
//        
//        // start node as it was
//        startNode = completed.get(0);
//        
//        assertNotNull(startNode);
//        assertEquals("StartProcess", startNode.getName());
//        assertEquals("StartEvent_1", startNode.getNodeId());
//        assertEquals(null, startNode.getConnection());
//        
//        startNode = completed.get(1);
//        
//        assertNotNull(startNode);
//        assertEquals("StartProcess", startNode.getName());
//        assertEquals("StartEvent_1", startNode.getNodeId());
//        assertNotNull(startNode.getConnection());
//        assertEquals("StartEvent_1-IntermediateCatchEvent_1", startNode.getConnection());
//        
//        // signal
//        NodeInstanceDesc signalNode = completed.get(2);
//        
//        assertNotNull(signalNode);
//        assertEquals("Catch", signalNode.getName());
//        assertEquals("IntermediateCatchEvent_1", signalNode.getNodeId());
//        assertNotNull(signalNode.getConnection());
//        assertEquals("StartEvent_1-IntermediateCatchEvent_1", signalNode.getConnection());
//        
//        signalNode = completed.get(3);
//        
//        assertNotNull(signalNode);
//        assertEquals("Catch", signalNode.getName());
//        assertEquals("IntermediateCatchEvent_1", signalNode.getNodeId());
//        assertNotNull(signalNode.getConnection());
//        assertEquals("IntermediateCatchEvent_1-ScriptTask_1", signalNode.getConnection());
//        
//        
//        //script
//        NodeInstanceDesc scriptNode = completed.get(4);
//        
//        assertNotNull(scriptNode);
//        assertEquals("Script Task", scriptNode.getName());
//        assertEquals("ScriptTask_1", scriptNode.getNodeId());
//        assertNotNull(scriptNode.getConnection());
//        assertEquals("IntermediateCatchEvent_1-ScriptTask_1", scriptNode.getConnection());
//        
//        scriptNode = completed.get(5);
//        
//        assertNotNull(scriptNode);
//        assertEquals("Script Task", scriptNode.getName());
//        assertEquals("ScriptTask_1", scriptNode.getNodeId());
//        assertNotNull(scriptNode.getConnection());
//        assertEquals("ScriptTask_1-EndEvent_1", scriptNode.getConnection());
//        
//        
//        //end
//        NodeInstanceDesc endNode = completed.get(6);
//        
//        assertNotNull(endNode);
//        assertEquals("EndProcess", endNode.getName());
//        assertEquals("EndEvent_1", endNode.getNodeId());
//        assertNotNull(endNode.getConnection());
//        assertEquals("ScriptTask_1-EndEvent_1", endNode.getConnection());
//        
//        endNode = completed.get(7);
//        
//        assertNotNull(endNode);
//        assertEquals("EndProcess", endNode.getName());
//        assertEquals("EndEvent_1", endNode.getNodeId());
//        assertNull(endNode.getConnection());
//       
//        
//        pi = ksession.getProcessInstance(pi.getId());
//        assertEquals(null, pi);
//    }
//    
//    @Test
//    public void testGetActiveNodes() {
//        
//        KieSession ksession = sessionManager.getKsessionById(1);
//        ProcessInstance pi = ksession.startProcess("signal");
//        
//        Collection<NodeInstanceDesc> executedNodes = dataService.getProcessInstanceActiveNodes(1, pi.getId());
//        assertNotNull(executedNodes);
//        assertEquals(1, executedNodes.size());
//        NodeInstanceDesc signalNode = executedNodes.iterator().next();
//        
//        assertNotNull(signalNode);
//        assertEquals("Catch", signalNode.getName());
//        assertEquals("IntermediateCatchEvent_1", signalNode.getNodeId());
//        assertNotNull(signalNode.getConnection());
//        assertEquals("StartEvent_1-IntermediateCatchEvent_1", signalNode.getConnection());
//        
//        ksession.signalEvent("MySignal", null, pi.getId());
//        
//        pi = ksession.getProcessInstance(pi.getId());
//        assertEquals(null, pi);
//    }
//}

package org.droolsjbpm.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.droolsjbpm.services.api.Domain;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

public abstract class KnowledgeDataServiceBaseTest {

    @Inject
    protected transient TaskServiceEntryPoint taskService;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    protected KnowledgeDataService dataService;
    @Inject
    protected KnowledgeAdminDataService adminDataService;
    @Inject
    private FileService fs;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private KnowledgeDomainService domainService;
    
    @Before
    public void setup() {
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);
        sessionManager.buildSession("myKsession", "examples/general/", true);
    }
    
    @After
    public void teardown() {
        
    }
    
    @Test
    public void testGetProcessesByFilter() {
        assertNotNull(dataService);
        
        // find by complete id
        Collection<ProcessDesc> foundProcesses = dataService.getProcessesByFilter("signal");
        
        assertNotNull(foundProcesses);
        assertEquals(1, foundProcesses.size());
        
        // find by partial id
        foundProcesses = dataService.getProcessesByFilter("sign");
        
        assertNotNull(foundProcesses);
        assertEquals(1, foundProcesses.size());
        
        // find by complete name
        foundProcesses = dataService.getProcessesByFilter("Default Process");
        
        assertNotNull(foundProcesses);
        assertEquals(1, foundProcesses.size());
        
        // find by complete name
        foundProcesses = dataService.getProcessesByFilter("Default ");
        
        assertNotNull(foundProcesses);
        assertEquals(1, foundProcesses.size());
    }
    
    @Test
    public void testGetProcessInstancesByFilter() {
        assertNotNull(dataService);
        List<Integer> states = new ArrayList<Integer>();
        states.add(ProcessInstance.STATE_ACTIVE);
        
        // search on empty db
        Collection<ProcessInstanceDesc> foundInstances = dataService.getProcessInstances(states, null);
        
        assertNotNull(foundInstances);
        assertEquals(0, foundInstances.size());
        
        StatefulKnowledgeSession ksession = sessionManager.getKsessionById(1);
        ksession.startProcess("signal");
        
        // find process just by state
        foundInstances = dataService.getProcessInstances(states, null);
        
        assertNotNull(foundInstances);
        assertEquals(1, foundInstances.size()); 
        
        // find process instances by complete process id
        foundInstances = dataService.getProcessInstancesByProcessId(states, "signal", null);
        
        assertNotNull(foundInstances);
        assertEquals(1, foundInstances.size());
        
        // find process instances by partial process id
        foundInstances = dataService.getProcessInstancesByProcessId(states, "sig", null);
        
        assertNotNull(foundInstances);
        assertEquals(1, foundInstances.size());
        
        // find process instances by complete process name
        foundInstances = dataService.getProcessInstancesByProcessName(states, "Default Process", null);
        
        assertNotNull(foundInstances);
        assertEquals(1, foundInstances.size());
        
        // find process instances by partial process name
        foundInstances = dataService.getProcessInstancesByProcessName(states, "Default ", null);
        
        assertNotNull(foundInstances);
        assertEquals(1, foundInstances.size());
    }
}

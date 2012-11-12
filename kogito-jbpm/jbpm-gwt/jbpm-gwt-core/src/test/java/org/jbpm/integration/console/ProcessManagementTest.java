package org.jbpm.integration.console;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef.RESULT;
import org.jboss.bpm.console.client.model.ProcessInstanceRef.STATE;
import org.jbpm.integration.JbpmGwtCoreTestCase;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.NodeInstance;

public class ProcessManagementTest extends JbpmGwtCoreTestCase {

	ProcessManagement processManager = new ProcessManagement();

	@Before
	public void instantiateProcesses() {
		processManager = new ProcessManagement();
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("key", "variable");
		processManager.newInstance("Minimal");
		processManager.newInstance("UserTask", variables);
		processManager.newInstance("UserTask", variables);
	}

	@After
	public void clearProcesses() {
		JPAProcessInstanceDbLog.clear();
		processManager = null;
	}

	@Test @Ignore
	public void testSignalExecution() {
		// TODO implement
	}

	@Test
	public void testGetProcessDefinitions() {
	    List<ProcessDefinitionRef> processes = processManager.getProcessDefinitions();
	    boolean minimalProcessFound = false;
	    boolean userTaskProcDefFound = false;
	    for( ProcessDefinitionRef process : processes ) { 
	        if( "Minimal".equals(process.getId()) ) { 
	            minimalProcessFound = true;
	        }
	        else if( "UserTask".equals(process.getId()) ) { 
	            userTaskProcDefFound = true;
	        }
	    }
	    assertTrue("UserTask process definition not found", userTaskProcDefFound );
	    assertTrue("Minimal process definition not found", minimalProcessFound );
	}

	@Test
	public void testGetProcessDefinition() {
		assertEquals("UserTask", processManager.getProcessDefinition("UserTask").getId());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveProcessDefinition() {
		assertEquals(1, processManager.removeProcessDefinition("Minimal").size());
	}

	@Test 
	public void testGetProcessInstances() {
		List<ProcessInstanceRef> userTaskInstances = processManager.getProcessInstances("UserTask");
		List<ProcessInstanceRef> minimalInstances = processManager.getProcessInstances("Minimal");
		assertEquals(2, userTaskInstances.size());
		assertEquals(0, minimalInstances.size());
	}

	@Test 
	public void testNewProcessInstance() {
		assertEquals("UserTask", processManager.newInstance("UserTask").getDefinitionId());
		assertEquals(3, processManager.getProcessInstances("UserTask").size());
	}

	@Test 
	public void testNewProcessInstanceWithVariables() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("key2", "variable2");
		ProcessInstanceRef instanceRef = processManager.newInstance("UserTask",	variables);
		assertEquals("UserTask", instanceRef.getDefinitionId());
		assertEquals(3, processManager.getProcessInstances("UserTask").size());
		assertEquals("variable2", processManager.getInstanceData(instanceRef.getId()).get("key2"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSetProcessState() {
		processManager.setProcessState("23", STATE.RUNNING);
	}

	@Test
	public void testGetInstanceData() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("key2", "variable2");
		String instanceID = processManager.newInstance("UserTask", variables).getId();
		assertEquals(variables, processManager.getInstanceData(instanceID));
	}

	@Test
	public void testSetInstanceData() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("key", "variable2");
		String instanceID = processManager.newInstance("UserTask", variables).getId();
		variables.put("key3", "variable3");
		processManager.setInstanceData(instanceID, variables);
		assertEquals(variables, processManager.getInstanceData(instanceID));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteInstance() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("key3", "variable3");
		String instanceID = processManager.newInstance("UserTask", variables).getId();
		processManager.deleteInstance(instanceID);
		processManager.getInstanceData(instanceID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEndInstance() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("key3", "variable3");
		String instanceID = processManager.newInstance("UserTask", variables).getId();
		processManager.endInstance(instanceID, RESULT.ERROR);
		assertEquals(false, processManager.getInstanceData(instanceID).isEmpty());
	}

    @Test 
    public void testNewInstance() throws Exception {
        StatefulKnowledgeSession session = StatefulKnowledgeSessionUtil.getStatefulKnowledgeSession();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "krisv");
        params.put("reason", "Yearly performance evaluation");
        
        String definitionId = "Evaluation";
        
        ProcessInstanceLog processInstance = CommandDelegate.startProcess(definitionId, params);
        Collection<NodeInstance> activeNodes = CommandDelegate.getActiveNodeInstances(processInstance.getId());
        assertNotNull(activeNodes);
        Transform.processInstance(processInstance, activeNodes);
    }
}

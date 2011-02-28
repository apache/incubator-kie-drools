package org.jbpm.integration.console;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef.RESULT;
import org.jboss.bpm.console.client.model.ProcessInstanceRef.STATE;
import org.jbpm.integration.JbpmTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProcessManagementTest extends JbpmTestCase{

		ProcessManagement processManager = new ProcessManagement();
		@Before
		public void instantiateProcesses(){
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("key", "variable");
			processManager.newInstance("Minimal");
			processManager.newInstance("UserTask", variables);
			processManager.newInstance("UserTask", variables);
		
		}
		@After
		public void clearProcesses(){
			
			for ( ProcessInstanceRef instance :processManager.getProcessInstances("UserTask")){
				processManager.deleteInstance(instance.getId());
			}
			
		}
		
		@Test
		public void testGetProcessDefinitions(){
			assertEquals("UserTask",processManager.getProcessDefinitions().get(0).getId());
			assertEquals("Minimal",processManager.getProcessDefinitions().get(1).getId());
		}
		
		@Test
		public void testGetProcessDefinition(){
			assertEquals("UserTask",processManager.getProcessDefinition("UserTask").getId());
		}
		
		@Test (expected=UnsupportedOperationException.class)
		public void testRemoveProcessDefinition(){
			assertEquals(1,processManager.removeProcessDefinition("Minimal").size());
		}
		
		@Test
		public void testGetProcessInstance(){
			
			assertEquals("Minimal",processManager.getProcessInstance("1").getDefinitionId());
		}
		
		@Test
		public void testGetProcessInstances(){

			List<ProcessInstanceRef> userTaskInstances = processManager.getProcessInstances("UserTask");
			List<ProcessInstanceRef> minimalInstances = processManager.getProcessInstances("Minimal");
			assertEquals(2,userTaskInstances.size());
			assertEquals(0,minimalInstances.size());
		}
		
		@Test
		public void testNewProcessInstance(){
			assertEquals("UserTask" , processManager.newInstance("UserTask").getDefinitionId());
			assertEquals(3, processManager.getProcessInstances("UserTask").size());
		}
		
		@Test
		public void testNewProcessInstanceWithVariables(){
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("key2", "variable2");
			ProcessInstanceRef instanceRef= processManager.newInstance("UserTask",variables);
		
			assertEquals("UserTask" , instanceRef.getDefinitionId());
			assertEquals(3, processManager.getProcessInstances("UserTask").size());
			assertEquals("variable2", processManager.getInstanceData(instanceRef.getId()).get("key2"));
		}
		
		@Test (expected=UnsupportedOperationException.class)
		public void testSetProcessState(){
			
			processManager.setProcessState("23",STATE.RUNNING);
		}
		
		
		@Test
		public void testGetInstanceData() {
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("key2", "variable2");
			String instanceID =  processManager.newInstance("UserTask",variables).getId();
			
			assertEquals(variables , processManager.getInstanceData(instanceID));
		}
		
		@Test
		public void testSetInstanceData() {
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("key", "variable2");
			String instanceID =  processManager.newInstance("UserTask",variables).getId();
			variables.put("key3", "variable3");
			
			processManager.setInstanceData(instanceID, variables);
			assertEquals(variables , processManager.getInstanceData(instanceID));
		}
		
		@Test
		public void testSignalExecution() {
			//TODO implement
		}
		
		@Test(expected=IllegalArgumentException.class)
		public void testDeleteInstance(){
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("key3", "variable3");
			String instanceID =  processManager.newInstance("UserTask",variables).getId();
			
			
			processManager.deleteInstance(instanceID);
			processManager.getInstanceData(instanceID);
		}
		
		@Test(expected = IllegalArgumentException.class)
		public void testEndInstance(){
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("key3", "variable3");
			String instanceID =  processManager.newInstance("UserTask",variables).getId();
			
			
			processManager.endInstance(instanceID,RESULT.ERROR);
			assertEquals(false, processManager.getInstanceData(instanceID).isEmpty());
		}
		
}

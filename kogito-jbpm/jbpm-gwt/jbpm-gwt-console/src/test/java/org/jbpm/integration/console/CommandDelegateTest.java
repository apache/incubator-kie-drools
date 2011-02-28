package org.jbpm.integration.console;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.jbpm.integration.JbpmTestCase;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.Test;


public class CommandDelegateTest extends JbpmTestCase{
	private CommandDelegate delegate = new CommandDelegate();
	
	
	@Test
	public void testGetProcesses() {
		assertEquals("Minimal Process" ,delegate.getProcesses().get(1).getName());
		
	}
	
	@Test
	public void testGetProcess() {
		
		
		assertEquals("Minimal Process" ,delegate.getProcess("Minimal").getName());
		
	}
	@Test
	public void testGetProcessByName(){
		
		assertEquals("Minimal" ,delegate.getProcessByName("Minimal Process").getId());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveProcess(){
		
		delegate.removeProcess("312");
	}	
	
	@Test
	public void testStartInstance(){
		ProcessInstanceLog instance =   delegate.startProcess("Minimal", null);
		assertEquals(1, instance.getProcessInstanceId());
	}
	@Test
	public void testGetProcessInstanceLog() {
		ProcessInstanceLog instance =   delegate.startProcess("Minimal", null);
		assertEquals(instance.getId(), delegate.getProcessInstanceLog("2").getId());
	}
	
	@Test
	public void testGetProcessInstanceLogsByProcessId(){
		
		assertEquals(1,delegate.getProcessInstanceLogsByProcessId("Minimal").get(0).getId());
	}
	
	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testAbortProcessInstance(){
		HashMap<String,Object> variables = new HashMap<String, Object>();
		variables.put("key", "value");
		
		delegate.startProcess("UserTask", variables);
		
		delegate.abortProcessInstance("3");
		delegate.getProcessInstanceVariables("3");
		

		
	}
	@Test
	public void testGetProcessInstanceVariables(){
		HashMap<String,Object> variables = new HashMap<String, Object>();
		variables.put("key", "value");
		
		delegate.startProcess("UserTask", variables);
		
		assertEquals(variables, delegate.getProcessInstanceVariables("4"));
	}
	
	@Test
	public void testSetProcessInstanceVariables(){
		HashMap<String,Object> newVariables = new HashMap<String, Object>();
		newVariables.put("key", "value2");
		
		delegate.setProcessInstanceVariables("4", newVariables);
		assertEquals(newVariables, delegate.getProcessInstanceVariables("4"));
	}
	
	@Test
	public void testSignalExecution(){
		//TODO Implement
	}
}
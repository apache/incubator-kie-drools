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
		ProcessInstanceLog instance = delegate.startProcess("Minimal", null);
		assertEquals("Minimal", instance.getProcessId());
	}
	
	@Test
	public void testGetProcessInstanceLog() {
		ProcessInstanceLog instance = delegate.startProcess("Minimal", null);
		assertEquals(instance.getId(), delegate.getProcessInstanceLog(instance.getProcessInstanceId() + "").getId());
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
		
		ProcessInstanceLog instance = delegate.startProcess("UserTask", variables);
		
		assertEquals(variables, delegate.getProcessInstanceVariables(instance.getProcessInstanceId() + ""));
	}
	
	@Test
	public void testSetProcessInstanceVariables(){
		ProcessInstanceLog instance = delegate.startProcess("UserTask", null);
		HashMap<String,Object> newVariables = new HashMap<String, Object>();
		newVariables.put("key", "value2");
		delegate.setProcessInstanceVariables(instance.getId() + "", newVariables);
		assertEquals(newVariables, delegate.getProcessInstanceVariables(instance.getId() + ""));
	}
	
	@Test
	public void testSignalExecution(){
		//TODO Implement
	}
}
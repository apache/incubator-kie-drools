package org.jbpm.integration.console;

import java.util.HashMap;

import org.jbpm.integration.JbpmGwtCoreTestCase;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.Test;


public class CommandDelegateTest extends JbpmGwtCoreTestCase {
    
	@Test
	public void testGetProcesses() {
		assertEquals("Minimal Process", CommandDelegate.getProcesses().get(1).getName());
	}
	
	@Test
	public void testGetProcess() {
		assertEquals("Minimal Process" , CommandDelegate.getProcess("Minimal").getName());
	}
	
	@Test
	public void testGetProcessByName(){
		assertEquals("Minimal", CommandDelegate.getProcessByName("Minimal Process").getId());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveProcess(){
	    CommandDelegate.removeProcess("312");
	}	
	
	@Test
	public void testStartInstance(){
		ProcessInstanceLog instance = CommandDelegate.startProcess("Minimal", null);
		assertEquals("Minimal", instance.getProcessId());
	}
	
	@Test
	public void testGetProcessInstanceLog() {
		ProcessInstanceLog instance =  CommandDelegate.startProcess("Minimal", null);
		assertEquals(instance.getId(), CommandDelegate.getProcessInstanceLog(instance.getProcessInstanceId() + "").getId());
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testAbortProcessInstance(){
	    HashMap<String,Object> variables = new HashMap<String, Object>();
	    variables.put("key", "value");

	    CommandDelegate.startProcess("UserTask", variables);

	    CommandDelegate.abortProcessInstance("3");
	    CommandDelegate.getProcessInstanceVariables("3");
	}

	@Test
	public void testGetProcessInstanceVariables(){
		HashMap<String,Object> variables = new HashMap<String, Object>();
		variables.put("key", "value");
		
		ProcessInstanceLog instance = CommandDelegate.startProcess("UserTask", variables);
		
		assertEquals(variables, CommandDelegate.getProcessInstanceVariables(instance.getProcessInstanceId() + ""));
	}
	
	@Test
	public void testSetProcessInstanceVariables(){
		ProcessInstanceLog instance = CommandDelegate.startProcess("UserTask", null);
		HashMap<String,Object> newVariables = new HashMap<String, Object>();
		newVariables.put("key", "value2");
		CommandDelegate.setProcessInstanceVariables(instance.getId() + "", newVariables);
		assertEquals(newVariables, CommandDelegate.getProcessInstanceVariables(instance.getId() + ""));
	}
	
	@Test
	public void testSignalExecution(){
		//TODO Implement
	}
}
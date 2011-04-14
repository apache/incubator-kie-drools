package org.jbpm.integration.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jbpm.integration.JbpmTestCase;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.Ignore;
import org.junit.Test;

public class TransformTest extends JbpmTestCase {

	CommandDelegate delegate = new CommandDelegate();
	
	@Test
	public void testProcessDefinition(){
		
		org.drools.definition.process.Process process =   delegate.getProcess("Minimal");
		ProcessDefinitionRef processDefinitionRef = Transform.processDefinition(process);
		assertEquals(processDefinitionRef.getId(),process.getId());
		assertEquals(processDefinitionRef.getPackageName(),process.getPackageName());
		assertEquals(processDefinitionRef.getName(),process.getName());
	}
	
	@Test
	public void testProcessInstance(){
		String instanceID = Long.toString(delegate.startProcess("UserTask", null).getId());
		ProcessInstanceLog instanceLog =delegate.getProcessInstanceLog(instanceID);
		ProcessInstanceRef processInstanceRef = Transform.processInstance(instanceLog);
		
		assertEquals(instanceLog.getProcessInstanceId(),Long.parseLong(processInstanceRef.getId()));
		assertEquals(instanceLog.getProcessId(),processInstanceRef.getDefinitionId());
		
	}
	
	@Test @Ignore
	public void testTaskSummaryTransform(){
		fail("Unimplemented");
	}
	 
	@Test @Ignore
	public void testTaskTransform(){
		fail("Unimplemented");
	}
	
}
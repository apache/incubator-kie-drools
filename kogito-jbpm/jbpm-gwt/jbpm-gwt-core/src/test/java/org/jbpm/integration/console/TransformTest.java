package org.jbpm.integration.console;

import java.util.Collection;

import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jbpm.integration.JbpmGwtCoreTestCase;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.runtime.process.NodeInstance;

public class TransformTest extends JbpmGwtCoreTestCase {

	@Test
	public void testProcessDefinition(){
		org.kie.definition.process.Process process = CommandDelegate.getProcess("Minimal");
		ProcessDefinitionRef processDefinitionRef = Transform.processDefinition(process);
		assertEquals(processDefinitionRef.getId(),process.getId());
		assertEquals(processDefinitionRef.getPackageName(),process.getPackageName());
		assertEquals(processDefinitionRef.getName(),process.getName());
	}
	
	@Test
	public void testProcessInstance(){
		String instanceID = Long.toString(CommandDelegate.startProcess("UserTask", null).getProcessInstanceId());
		ProcessInstanceLog instanceLog = CommandDelegate.getProcessInstanceLog(instanceID);
		ProcessInstanceRef processInstanceRef = Transform.processInstance(instanceLog, null);
		
		assertEquals(instanceLog.getProcessInstanceId(),Long.parseLong(processInstanceRef.getId()));
		assertEquals(instanceLog.getProcessId(),processInstanceRef.getDefinitionId());
		
	}
	
	@Test
    public void testProcessInstanceWithActiveNodesSignalEvent(){
        String instanceID = Long.toString(CommandDelegate.startProcess("SignalEvent", null).getProcessInstanceId());
        ProcessInstanceLog instanceLog = CommandDelegate.getProcessInstanceLog(instanceID);
        Collection<NodeInstance> activeNodes = CommandDelegate.getActiveNodeInstances(Long.parseLong(instanceID)); 
        ProcessInstanceRef processInstanceRef = Transform.processInstance(instanceLog, activeNodes);
        
        assertEquals(instanceLog.getProcessInstanceId(),Long.parseLong(processInstanceRef.getId()));
        assertEquals(instanceLog.getProcessId(),processInstanceRef.getDefinitionId());
        assertEquals(1,processInstanceRef.getRootToken().getChildren().size());
        assertEquals("Signal",processInstanceRef.getRootToken().getChildren().get(0).getCurrentNodeName());
        assertEquals("Signal_1",processInstanceRef.getRootToken().getChildren().get(0).getName());
        assertTrue(processInstanceRef.getRootToken().getChildren().get(0).canBeSignaled());
        
    }
	
   @Test
    public void testProcessInstanceWithActiveNodesMessageEvent(){
        String instanceID = Long.toString(CommandDelegate.startProcess("MessageEvent", null).getProcessInstanceId());
        ProcessInstanceLog instanceLog = CommandDelegate.getProcessInstanceLog(instanceID);
        Collection<NodeInstance> activeNodes = CommandDelegate.getActiveNodeInstances(Long.parseLong(instanceID)); 
        ProcessInstanceRef processInstanceRef = Transform.processInstance(instanceLog, activeNodes);
        
        assertEquals(instanceLog.getProcessInstanceId(),Long.parseLong(processInstanceRef.getId()));
        assertEquals(instanceLog.getProcessId(),processInstanceRef.getDefinitionId());
        assertEquals(1,processInstanceRef.getRootToken().getChildren().size());
//        assertEquals("Signal",processInstanceRef.getRootToken().getChildren().get(0).getCurrentNodeName());
//        assertEquals("Signal_1",processInstanceRef.getRootToken().getChildren().get(0).getName());
        assertTrue(!processInstanceRef.getRootToken().getChildren().get(0).canBeSignaled());
        
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
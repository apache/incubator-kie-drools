package org.jbpm.integration.console;


import org.jbpm.integration.JbpmTestCase;
import org.jbpm.task.service.BaseHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskManagementTest extends JbpmTestCase {
	TaskManagement taskManager;
	@Before
	public void instanitateManager(){
		taskManager = new TaskManagement();
		
	}
	@After
	public void destroyManager(){
		taskManager= null;
	}
	@Test
	public void testAssignTask(){
		taskManager.connect();
	
	}
	
	
}

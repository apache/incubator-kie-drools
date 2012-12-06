package org.jbpm.integration.console;

import org.jbpm.integration.JbpmGwtCoreTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskManagementTest extends JbpmGwtCoreTestCase {
	
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

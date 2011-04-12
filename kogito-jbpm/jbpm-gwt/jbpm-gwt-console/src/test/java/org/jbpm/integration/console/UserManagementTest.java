package org.jbpm.integration.console;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserManagementTest {
	 UserManagement userManager = new UserManagement();
	
	@Test
	public void testGetActorsForGroup(){
		fail("Unimplemented");
		assertEquals(true,userManager.getActorsForGroup("Knights Templar").contains("krisv"));
		
	}
	
	@Test
	public void testGetGroupsForActor(){
		fail("Unimplemented");
		assertEquals(true, userManager.getActorsForGroup("krisv").contains("Knights Templar"));
	}
}   

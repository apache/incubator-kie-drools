package org.jbpm.process.workitem;

import java.util.Collection;


import junit.framework.TestCase;

public class WorkItemRepositoryTest extends TestCase {
	
	public void testEmpty() {
	}
	
	public void FIXMEtestWorkItemRepository() {
		String path = "/NotBackedUp/development/projects/jbpm-workitems-repository";
		Collection<WorkDefinitionImpl> workDefinitions = WorkItemRepository.getWorkDefinitions(path).values();
		assertEquals(1, workDefinitions.size());
		assertEquals("Email", workDefinitions.iterator().next().getName());
	}

}

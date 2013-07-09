package org.jbpm.process.workitem;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Ignore;
import org.junit.Test;

public class WorkItemRepositoryTest extends AbstractBaseTest {
	
	
	@Test
	@Ignore
	public void FIXMEtestWorkItemRepository() {
		String path = "/NotBackedUp/development/projects/jbpm-workitems-repository";
		Collection<WorkDefinitionImpl> workDefinitions = WorkItemRepository.getWorkDefinitions(path).values();
		assertEquals(1, workDefinitions.size());
		assertEquals("Email", workDefinitions.iterator().next().getName());
	}

}

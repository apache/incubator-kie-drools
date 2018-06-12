/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.wih.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;


public class PeopleAssignmentHelperTest  extends AbstractBaseTest {
	
	private PeopleAssignmentHelper peopleAssignmentHelper;
	
	@Before
	public void setup() {
		
		peopleAssignmentHelper = new PeopleAssignmentHelper();
		
	}
	
	@Test
	public void testProcessPeopleAssignments() {

		List<OrganizationalEntity> organizationalEntities = new ArrayList<OrganizationalEntity>();
		
		String ids = "espiegelberg,   drbug   ";
		assertTrue(organizationalEntities.isEmpty());
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
		assertTrue(organizationalEntities.size() == 2);
		assertTrue(organizationalEntities.contains(createUser("drbug")));
		assertTrue(organizationalEntities.contains(createUser("espiegelberg")));
		
		ids = null;
		organizationalEntities = new ArrayList<OrganizationalEntity>();
		assertTrue(organizationalEntities.isEmpty());
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
		assertTrue(organizationalEntities.isEmpty());
		
		ids = "     ";
		organizationalEntities = new ArrayList<OrganizationalEntity>();
		assertTrue(organizationalEntities.isEmpty());
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
		assertTrue(organizationalEntities.isEmpty());
		
		ids = "Software Developer";
		organizationalEntities = new ArrayList<OrganizationalEntity>();
		assertTrue(organizationalEntities.isEmpty());
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, false);
		assertTrue(organizationalEntities.size() == 1);
		assertTrue(organizationalEntities.contains(createGroup("Software Developer")));
		
		// Test that a duplicate is not added; only 1 of the 2 passed in should be added
		ids = "Software Developer,Project Manager";
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, false);
		assertTrue(organizationalEntities.size() == 2);
		assertTrue(organizationalEntities.contains(createGroup("Software Developer")));
		assertTrue(organizationalEntities.contains(createGroup("Project Manager")));
		
	}

	@Test
	public void testProcessPeopleAssignmentsWithEmptyStrings() {

		List<OrganizationalEntity> organizationalEntities = new ArrayList<OrganizationalEntity>();

		String ids = "bpmsAdmin,";
		assertTrue(organizationalEntities.isEmpty());
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
		assertTrue(organizationalEntities.size() == 1);
		assertTrue(organizationalEntities.contains(createUser("bpmsAdmin")));

		ids = ",bpmsAdmin";
		organizationalEntities = new ArrayList<OrganizationalEntity>();
		assertTrue(organizationalEntities.isEmpty());
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
		assertTrue(organizationalEntities.size() == 1);
		assertTrue(organizationalEntities.contains(createUser("bpmsAdmin")));
	}
	
	@Test
	public void testAssignActors() {
		
		String actorId = "espiegelberg";
		
		Task task = TaskModelProvider.getFactory().newTask();
		InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData();
		PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
		
		WorkItem workItem = new WorkItemImpl();		
		workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
		
		peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
		OrganizationalEntity organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
		assertTrue(organizationalEntity1 instanceof User);
		assertEquals(actorId, organizationalEntity1.getId());		
		assertEquals(actorId, taskData.getCreatedBy().getId());
		
		workItem = new WorkItemImpl();
		peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);				
		workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId + ", drbug  ");
		peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
		assertEquals(2, peopleAssignments.getPotentialOwners().size());
		organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
		assertEquals(actorId, organizationalEntity1.getId());		
		assertEquals(actorId, taskData.getCreatedBy().getId());
		OrganizationalEntity organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
		assertEquals("drbug", organizationalEntity2.getId());

		workItem = new WorkItemImpl();
		peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);				
		workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, "");
		peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
		assertEquals(0, peopleAssignments.getPotentialOwners().size());

	}
	
	@Test
    public void testAssignActorsWithCustomSeparatorViaSysProp() {
        System.setProperty("org.jbpm.ht.user.separator", ";");
        peopleAssignmentHelper = new PeopleAssignmentHelper();
        String actorId = "user1;user2";
        
        Task task = TaskModelProvider.getFactory().newTask();
		InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData();
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        
        WorkItem workItem = new WorkItemImpl();     
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
        
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        assertTrue(organizationalEntity1 instanceof User);
        assertEquals("user1", organizationalEntity1.getId());       
        assertEquals("user1", taskData.getCreatedBy().getId());
        
        OrganizationalEntity organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        assertTrue(organizationalEntity2 instanceof User);
        assertEquals("user2", organizationalEntity2.getId());       
        
        workItem = new WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);              
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId + "; drbug  ");
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        assertEquals(3, peopleAssignments.getPotentialOwners().size());
        organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        assertEquals("user1", organizationalEntity1.getId());       
        assertEquals("user1", taskData.getCreatedBy().getId());
        organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        assertTrue(organizationalEntity2 instanceof User);
        assertEquals("user2", organizationalEntity2.getId()); 
        OrganizationalEntity organizationalEntity3 = peopleAssignments.getPotentialOwners().get(2);
        assertEquals("drbug", organizationalEntity3.getId());

        workItem = new WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);              
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, "");
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        assertEquals(0, peopleAssignments.getPotentialOwners().size());
        System.clearProperty("org.jbpm.ht.user.separator");
    }
	
	@Test
    public void testAssignActorsWithCustomSeparator() {
        peopleAssignmentHelper = new PeopleAssignmentHelper(":");
        String actorId = "user1:user2";
        
        Task task = TaskModelProvider.getFactory().newTask();
		InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData();
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        
        WorkItem workItem = new WorkItemImpl();     
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
        
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        assertTrue(organizationalEntity1 instanceof User);
        assertEquals("user1", organizationalEntity1.getId());       
        assertEquals("user1", taskData.getCreatedBy().getId());
        
        OrganizationalEntity organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        assertTrue(organizationalEntity2 instanceof User);
        assertEquals("user2", organizationalEntity2.getId());       
        
        workItem = new WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);              
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId + ": drbug  ");
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        assertEquals(3, peopleAssignments.getPotentialOwners().size());
        organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
        assertEquals("user1", organizationalEntity1.getId());       
        assertEquals("user1", taskData.getCreatedBy().getId());
        organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
        assertTrue(organizationalEntity2 instanceof User);
        assertEquals("user2", organizationalEntity2.getId()); 
        OrganizationalEntity organizationalEntity3 = peopleAssignments.getPotentialOwners().get(2);
        assertEquals("drbug", organizationalEntity3.getId());

        workItem = new WorkItemImpl();
        peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);              
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, "");
        peopleAssignmentHelper.assignActors(workItem, peopleAssignments, taskData);
        assertEquals(0, peopleAssignments.getPotentialOwners().size());
    }
	
	@Test
	public void testAssignBusinessAdministrators() {
	
		String businessAdministratorId = "espiegelberg";
		
		Task task = TaskModelProvider.getFactory().newTask();
		PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
		
		WorkItem workItem = new WorkItemImpl();		
		workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_ID, businessAdministratorId);

		peopleAssignmentHelper.assignBusinessAdministrators(workItem, peopleAssignments);
		assertEquals(3, peopleAssignments.getBusinessAdministrators().size());
		OrganizationalEntity organizationalEntity1 = peopleAssignments.getBusinessAdministrators().get(0);
		assertTrue(organizationalEntity1 instanceof User);
		assertEquals("Administrator", organizationalEntity1.getId());

		OrganizationalEntity organizationalEntity2 = peopleAssignments.getBusinessAdministrators().get(1);        
        assertTrue(organizationalEntity2 instanceof Group);              
        assertEquals("Administrators", organizationalEntity2.getId());

        OrganizationalEntity organizationalEntity3 = peopleAssignments.getBusinessAdministrators().get(2);      
        assertTrue(organizationalEntity3 instanceof User);              
        assertEquals(businessAdministratorId, organizationalEntity3.getId());
	}

    @Test
    public void testAssignBusinessAdministratorGroups() {
    
        String businessAdministratorGroupId = "Super users";
        
        Task task = TaskModelProvider.getFactory().newTask();
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        
        WorkItem workItem = new WorkItemImpl();     
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_GROUP_ID, businessAdministratorGroupId);

        peopleAssignmentHelper.assignBusinessAdministrators(workItem, peopleAssignments);
        assertEquals(3, peopleAssignments.getBusinessAdministrators().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getBusinessAdministrators().get(0);
        assertTrue(organizationalEntity1 instanceof User);
        assertEquals("Administrator", organizationalEntity1.getId());

        OrganizationalEntity organizationalEntity2 = peopleAssignments.getBusinessAdministrators().get(1);        
        assertTrue(organizationalEntity2 instanceof Group);              
        assertEquals("Administrators", organizationalEntity2.getId());

        OrganizationalEntity organizationalEntity3 = peopleAssignments.getBusinessAdministrators().get(2);      
        assertTrue(organizationalEntity3 instanceof Group);              
        assertEquals(businessAdministratorGroupId, organizationalEntity3.getId());
    }
	
	@Test
	public void testAssignTaskstakeholders() {
	
		String taskStakeholderId = "espiegelberg";
		
		Task task = TaskModelProvider.getFactory().newTask();
		InternalPeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
		
		WorkItem workItem = new WorkItemImpl();		
		workItem.setParameter(PeopleAssignmentHelper.TASKSTAKEHOLDER_ID, taskStakeholderId);

		peopleAssignmentHelper.assignTaskStakeholders(workItem, peopleAssignments);
		assertEquals(1, peopleAssignments.getTaskStakeholders().size());
		OrganizationalEntity organizationalEntity1 = peopleAssignments.getTaskStakeholders().get(0);		
		assertTrue(organizationalEntity1 instanceof User);				
		assertEquals(taskStakeholderId, organizationalEntity1.getId());
		
	}
	
	@Test
	public void testAssignGroups() {
		
		String groupId = "Software Developers, Project Managers";
		
		Task task = TaskModelProvider.getFactory().newTask();
		PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
		
		WorkItem workItem = new WorkItemImpl();		
		workItem.setParameter(PeopleAssignmentHelper.GROUP_ID, groupId);
		
		peopleAssignmentHelper.assignGroups(workItem, peopleAssignments);
		OrganizationalEntity organizationalEntity1 = peopleAssignments.getPotentialOwners().get(0);
		assertTrue(organizationalEntity1 instanceof Group);
		assertEquals("Software Developers", organizationalEntity1.getId());
		OrganizationalEntity organizationalEntity2 = peopleAssignments.getPotentialOwners().get(1);
		assertTrue(organizationalEntity2 instanceof Group);
		assertEquals("Project Managers", organizationalEntity2.getId());
		
	}
	
	@Test
	public void testgetNullSafePeopleAssignments() {
		
		Task task = TaskModelProvider.getFactory().newTask();
		
		InternalPeopleAssignments peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
		assertNotNull(peopleAssignment);
		
		peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
		assertNotNull(peopleAssignment);
		
		((InternalTask) task).setPeopleAssignments(null);
		peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
		assertNotNull(peopleAssignment);
		assertEquals(0, peopleAssignment.getPotentialOwners().size());
		assertEquals(0, peopleAssignment.getBusinessAdministrators().size());
		assertEquals(0, peopleAssignment.getExcludedOwners().size());
		assertEquals(0, peopleAssignment.getRecipients().size());
		assertEquals(0, peopleAssignment.getTaskStakeholders().size());
		
	}	
	
	@Test
	public void testHandlePeopleAssignments() {
		
		InternalTask task = (InternalTask) TaskModelProvider.getFactory().newTask();
		InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData();
		InternalPeopleAssignments peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
		assertNotNull(peopleAssignment);
		assertEquals(0, peopleAssignment.getPotentialOwners().size());
		assertEquals(0, peopleAssignment.getBusinessAdministrators().size());
		assertEquals(0, peopleAssignment.getTaskStakeholders().size());
		
		String actorId = "espiegelberg";
		String taskStakeholderId = "drmary";
		String businessAdministratorId = "drbug";
        String businessAdministratorGroupId = "Super users";
        String excludedOwnerId = "john";
        String recipientId = "mary";
		
		WorkItem workItem = new WorkItemImpl();		
		workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
		workItem.setParameter(PeopleAssignmentHelper.TASKSTAKEHOLDER_ID, taskStakeholderId);
		workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_ID, businessAdministratorId);
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_GROUP_ID, businessAdministratorGroupId);
        workItem.setParameter(PeopleAssignmentHelper.EXCLUDED_OWNER_ID, excludedOwnerId);
        workItem.setParameter(PeopleAssignmentHelper.RECIPIENT_ID, recipientId);
		
		peopleAssignmentHelper.handlePeopleAssignments(workItem, task, taskData);
		
		List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
		assertEquals(1, potentialOwners.size());
		assertEquals(actorId, potentialOwners.get(0).getId());
		
		List<OrganizationalEntity> businessAdministrators = task.getPeopleAssignments().getBusinessAdministrators();
		assertEquals(4, businessAdministrators.size());
		assertEquals("Administrator", businessAdministrators.get(0).getId());
		// Admin group
		assertEquals("Administrators", businessAdministrators.get(1).getId());
		assertEquals(businessAdministratorId, businessAdministrators.get(2).getId());
        assertEquals(businessAdministratorGroupId, businessAdministrators.get(3).getId());
		
		
		List<OrganizationalEntity> taskStakehoders = ((InternalPeopleAssignments) task.getPeopleAssignments()).getTaskStakeholders();
		assertEquals(1, taskStakehoders.size());
		assertEquals(taskStakeholderId, taskStakehoders.get(0).getId());

        List<OrganizationalEntity> excludedOwners = ((InternalPeopleAssignments) task.getPeopleAssignments()).getExcludedOwners();
        assertEquals(1, excludedOwners.size());
        assertEquals(excludedOwnerId, excludedOwners.get(0).getId());

        List<OrganizationalEntity> recipients = ((InternalPeopleAssignments) task.getPeopleAssignments()).getRecipients();
        assertEquals(1, recipients.size());
        assertEquals(recipientId, recipients.get(0).getId());
		
	}

    @Test
    public void testHandleMultiPeopleAssignments() {

    	InternalTask task = (InternalTask) TaskModelProvider.getFactory().newTask();
		InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData();
        InternalPeopleAssignments peopleAssignment = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        assertNotNull(peopleAssignment);
        assertEquals(0, peopleAssignment.getPotentialOwners().size());
        assertEquals(0, peopleAssignment.getBusinessAdministrators().size());
        assertEquals(0, peopleAssignment.getTaskStakeholders().size());

        String actorId = "espiegelberg,john";
        String taskStakeholderId = "drmary,krisv";
        String businessAdministratorId = "drbug,peter";
        String businessAdministratorGroupId = "Super users,Flow administrators";
        String excludedOwnerId = "john,poul";
        String recipientId = "mary,steve";

        WorkItem workItem = new WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.ACTOR_ID, actorId);
        workItem.setParameter(PeopleAssignmentHelper.TASKSTAKEHOLDER_ID, taskStakeholderId);
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_ID, businessAdministratorId);
        workItem.setParameter(PeopleAssignmentHelper.BUSINESSADMINISTRATOR_GROUP_ID, businessAdministratorGroupId);
        workItem.setParameter(PeopleAssignmentHelper.EXCLUDED_OWNER_ID, excludedOwnerId);
        workItem.setParameter(PeopleAssignmentHelper.RECIPIENT_ID, recipientId);

        peopleAssignmentHelper.handlePeopleAssignments(workItem, task, taskData);

        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        assertEquals(2, potentialOwners.size());
        assertEquals("espiegelberg", potentialOwners.get(0).getId());
        assertEquals("john", potentialOwners.get(1).getId());

        List<OrganizationalEntity> businessAdministrators = task.getPeopleAssignments().getBusinessAdministrators();
        assertEquals(6, businessAdministrators.size());
        assertEquals("Administrator", businessAdministrators.get(0).getId());
        //Admin group
        assertEquals("Administrators", businessAdministrators.get(1).getId());
        assertEquals("drbug", businessAdministrators.get(2).getId());
        assertEquals("peter", businessAdministrators.get(3).getId());
        assertEquals("Super users", businessAdministrators.get(4).getId());
        assertEquals("Flow administrators", businessAdministrators.get(5).getId());
        
        
        List<OrganizationalEntity> taskStakehoders = ((InternalPeopleAssignments) task.getPeopleAssignments()).getTaskStakeholders();
        assertEquals(2, taskStakehoders.size());
        assertEquals("drmary", taskStakehoders.get(0).getId());
        assertEquals("krisv", taskStakehoders.get(1).getId());

        List<OrganizationalEntity> excludedOwners = ((InternalPeopleAssignments) task.getPeopleAssignments()).getExcludedOwners();
        assertEquals(2, excludedOwners.size());
        assertEquals("john", excludedOwners.get(0).getId());
        assertEquals("poul", excludedOwners.get(1).getId());

        List<OrganizationalEntity> recipients = ((InternalPeopleAssignments) task.getPeopleAssignments()).getRecipients();
        assertEquals(2, recipients.size());
        assertEquals("mary", recipients.get(0).getId());
        assertEquals("steve", recipients.get(1).getId());

    }

    @Test
    public void testAssignExcludedOwners() {

        String excludedOwnerId = "espiegelberg";

        Task task = TaskModelProvider.getFactory().newTask();
        InternalPeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);

        WorkItem workItem = new WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.EXCLUDED_OWNER_ID, excludedOwnerId);

        peopleAssignmentHelper.assignExcludedOwners(workItem, peopleAssignments);
        assertEquals(1, peopleAssignments.getExcludedOwners().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getExcludedOwners().get(0);
        assertTrue(organizationalEntity1 instanceof User);
        assertEquals(excludedOwnerId, organizationalEntity1.getId());

    }

    @Test
    public void testAssignRecipients() {

        String recipientId = "espiegelberg";

        Task task = TaskModelProvider.getFactory().newTask();
        InternalPeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);

        WorkItem workItem = new WorkItemImpl();
        workItem.setParameter(PeopleAssignmentHelper.RECIPIENT_ID, recipientId);

        peopleAssignmentHelper.assignRecipients(workItem, peopleAssignments);
        assertEquals(1, peopleAssignments.getRecipients().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getRecipients().get(0);
        assertTrue(organizationalEntity1 instanceof User);
        assertEquals(recipientId, organizationalEntity1.getId());

    }
    
    @Test
    public void testAssignBusinessAdministratorsChangedDefaults() {
        peopleAssignmentHelper = new PeopleAssignmentHelper("myadmin", "mygroup");
        
        
        Task task = TaskModelProvider.getFactory().newTask();
        PeopleAssignments peopleAssignments = peopleAssignmentHelper.getNullSafePeopleAssignments(task);
        
        WorkItem workItem = new WorkItemImpl();             

        peopleAssignmentHelper.assignBusinessAdministrators(workItem, peopleAssignments);
        assertEquals(2, peopleAssignments.getBusinessAdministrators().size());
        OrganizationalEntity organizationalEntity1 = peopleAssignments.getBusinessAdministrators().get(0);
        assertTrue(organizationalEntity1 instanceof User);
        assertEquals("myadmin", organizationalEntity1.getId());

        OrganizationalEntity organizationalEntity2 = peopleAssignments.getBusinessAdministrators().get(1);        
        assertTrue(organizationalEntity2 instanceof Group);              
        assertEquals("mygroup", organizationalEntity2.getId());
    }

    private User createUser(String id) {
        return TaskModelProvider.getFactory().newUser(id);
    }

    private Group createGroup(String id) {
        return TaskModelProvider.getFactory().newGroup(id);
    }
}

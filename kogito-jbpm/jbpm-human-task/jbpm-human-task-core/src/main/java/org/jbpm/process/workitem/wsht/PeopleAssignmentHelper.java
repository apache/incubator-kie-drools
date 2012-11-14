/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.process.workitem.wsht;

import java.util.ArrayList;
import java.util.List;

import org.kie.runtime.process.WorkItem;
import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;

/**
 * A class responsible for assigning the various ownerships (actors, groups, business 
 * administrators, and task stakeholders) from a <code>WorkItem</code> to a <code>Task</code>. 
 * This class consolidates common code for reuse across multiple <code>WorkItemHandler</code>s.
 *
 */
public class PeopleAssignmentHelper {

	public static final String ACTOR_ID = "ActorId";
	public static final String GROUP_ID = "GroupId";
	public static final String BUSINESSADMINISTRATOR_ID = "BusinessAdministratorId";
	public static final String TASKSTAKEHOLDER_ID = "TaskStakeholderId";
    public static final String EXCLUDED_OWNER_ID = "ExcludedOwnerId";
    public static final String RECIPIENT_ID = "RecipientId";
	
	public void handlePeopleAssignments(WorkItem workItem, Task task, TaskData taskData) {
		
		PeopleAssignments peopleAssignments = getNullSafePeopleAssignments(task);
        
		assignActors(workItem, peopleAssignments, taskData);
		assignGroups(workItem, peopleAssignments);		
		assignBusinessAdministrators(workItem, peopleAssignments);
		assignTaskStakeholders(workItem, peopleAssignments);
        assignExcludedOwners(workItem, peopleAssignments);
        assignRecipients(workItem, peopleAssignments);
		
		task.setPeopleAssignments(peopleAssignments);
        
	}
	
	protected void assignActors(WorkItem workItem, PeopleAssignments peopleAssignments, TaskData taskData) {
		
        String actorIds = (String) workItem.getParameter(ACTOR_ID);        
        List<OrganizationalEntity> potentialOwners = peopleAssignments.getPotentialOwners();
        
        processPeopleAssignments(actorIds, potentialOwners, true);

        // Set the first user as creator ID??? hmmm might be wrong
        if (potentialOwners.size() > 0) {
        	
        	OrganizationalEntity firstPotentialOwner = potentialOwners.get(0);
        	taskData.setCreatedBy((User) firstPotentialOwner);

        }
        
	}
	
	protected void assignGroups(WorkItem workItem, PeopleAssignments peopleAssignments) {
	
        String groupIds = (String) workItem.getParameter(GROUP_ID);
        List<OrganizationalEntity> potentialOwners = peopleAssignments.getPotentialOwners();
        
        processPeopleAssignments(groupIds, potentialOwners, false);
        
	}

	protected void assignBusinessAdministrators(WorkItem workItem, PeopleAssignments peopleAssignments) {
		
		String businessAdministratorIds = (String) workItem.getParameter(BUSINESSADMINISTRATOR_ID);
        List<OrganizationalEntity> businessAdministrators = peopleAssignments.getBusinessAdministrators();
        
        User administrator = new User("Administrator");        
        businessAdministrators.add(administrator);
        
        processPeopleAssignments(businessAdministratorIds, businessAdministrators, true);
        
	}
	
	protected void assignTaskStakeholders(WorkItem workItem, PeopleAssignments peopleAssignments) {
		
		String taskStakehodlerIds = (String) workItem.getParameter(TASKSTAKEHOLDER_ID);
		List<OrganizationalEntity> taskStakeholders = peopleAssignments.getTaskStakeholders();

		processPeopleAssignments(taskStakehodlerIds, taskStakeholders, true);
		
	}

    protected void assignExcludedOwners(WorkItem workItem, PeopleAssignments peopleAssignments) {

        String excludedOwnerIds = (String) workItem.getParameter(EXCLUDED_OWNER_ID);
        List<OrganizationalEntity> excludedOwners = peopleAssignments.getExcludedOwners();

        processPeopleAssignments(excludedOwnerIds, excludedOwners, true);

    }

    protected void assignRecipients(WorkItem workItem, PeopleAssignments peopleAssignments) {

        String recipientIds = (String) workItem.getParameter(RECIPIENT_ID);
        List<OrganizationalEntity> recipients = peopleAssignments.getRecipients();

        processPeopleAssignments(recipientIds, recipients, true);

    }

	protected void processPeopleAssignments(String peopleAssignmentIds, List<OrganizationalEntity> organizationalEntities, boolean user) {

        if (peopleAssignmentIds != null && peopleAssignmentIds.trim().length() > 0) {

            String[] ids = peopleAssignmentIds.split(",");
            for (String id : ids) {
                id = id.trim();
                boolean exists = false;
                for (OrganizationalEntity orgEntity : organizationalEntities) {
                    if (orgEntity.getId().equals(id)) {
                        exists = true;
                    }
                }

                if (!exists) {
                    OrganizationalEntity organizationalEntity = null;
                    if (user) {
                        organizationalEntity = new User(id);
                    } else {
                        organizationalEntity = new Group(id);
                    }
                    organizationalEntities.add(organizationalEntity);

                }
            }
        }
	}
	
	protected PeopleAssignments getNullSafePeopleAssignments(Task task) {
		
		PeopleAssignments peopleAssignments = task.getPeopleAssignments();
        
        if (peopleAssignments == null) {
        	
        	peopleAssignments = new PeopleAssignments();
        	peopleAssignments.setPotentialOwners(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setBusinessAdministrators(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setExcludedOwners(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setRecipients(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setTaskStakeholders(new ArrayList<OrganizationalEntity>());

        }
        
		return peopleAssignments;
		
	}
	
}
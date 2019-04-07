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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;


/**
 * A class responsible for assigning the various ownerships (actors, groups, business 
 * administrators, and task stakeholders) from a <code>WorkItem</code> to a <code>Task</code>. 
 * This class consolidates common code for reuse across multiple <code>WorkItemHandler</code>s.
 */
public class PeopleAssignmentHelper {

	public static final String ACTOR_ID = "ActorId";
	public static final String GROUP_ID = "GroupId";
	public static final String BUSINESSADMINISTRATOR_ID = "BusinessAdministratorId";
    public static final String BUSINESSADMINISTRATOR_GROUP_ID = "BusinessAdministratorGroupId";
	public static final String TASKSTAKEHOLDER_ID = "TaskStakeholderId";
    public static final String EXCLUDED_OWNER_ID = "ExcludedOwnerId";
    public static final String RECIPIENT_ID = "RecipientId";
    
    public static final String DEFAULT_ADMIN_USER = System.getProperty("org.jbpm.ht.admin.user", "Administrator");
    public static final String DEFAULT_ADMIN_GROUP = System.getProperty("org.jbpm.ht.admin.group", "Administrators");
    
    private String separator;
    private CaseData caseFile;
    
    private String administratorUser = DEFAULT_ADMIN_USER;
    private String administratorGroup = DEFAULT_ADMIN_GROUP;
    
    private Predicate<? super OrganizationalEntity> userFilter = (OrganizationalEntity oe) -> { return oe instanceof User;};
    private Predicate<? super OrganizationalEntity> groupFilter = (OrganizationalEntity oe) -> { return oe instanceof Group;};
    private Predicate<? super OrganizationalEntity> noFilter = (OrganizationalEntity oe) -> { return true;};
    
    public PeopleAssignmentHelper() {
        this.separator = System.getProperty("org.jbpm.ht.user.separator", ",");
    }
	
    public PeopleAssignmentHelper(String separator) {
        this.separator = separator;
    }
    
    public PeopleAssignmentHelper(String adminUser, String adminGroup) {
        this.administratorUser = adminUser;
        this.administratorGroup = adminGroup;
    }
    
    public PeopleAssignmentHelper(CaseData caseFile) {
        this();
        this.caseFile = caseFile;        
    }
    
	public void handlePeopleAssignments(WorkItem workItem, InternalTask task, InternalTaskData taskData) {
		
		InternalPeopleAssignments peopleAssignments = getNullSafePeopleAssignments(task);
        
		assignActors(workItem, peopleAssignments, taskData);
		assignGroups(workItem, peopleAssignments);		
		assignBusinessAdministrators(workItem, peopleAssignments);
		assignTaskStakeholders(workItem, peopleAssignments);
        assignExcludedOwners(workItem, peopleAssignments);
        assignRecipients(workItem, peopleAssignments);
		
		task.setPeopleAssignments(peopleAssignments);
        
	}
	@SuppressWarnings("unchecked")
	protected void assignActors(WorkItem workItem, PeopleAssignments peopleAssignments, InternalTaskData taskData) {
	    List<OrganizationalEntity> potentialOwners = peopleAssignments.getPotentialOwners();
        
	    Object actorIds = adjustParam(workItem.getParameter(ACTOR_ID), userFilter);        
	    
	    if (actorIds instanceof Collection) {
            processPeopleAssignments((Collection<OrganizationalEntity>)actorIds, potentialOwners);
        } else {
            processPeopleAssignments((String)actorIds, potentialOwners, true);
        }

        // Set the first user as creator ID??? hmmm might be wrong
        if (potentialOwners.size() > 0 && taskData.getCreatedBy() == null) {
        	
        	OrganizationalEntity firstPotentialOwner = potentialOwners.get(0);
        	taskData.setCreatedBy((User) firstPotentialOwner);

        }
        
	}
	@SuppressWarnings("unchecked")
	protected void assignGroups(WorkItem workItem, PeopleAssignments peopleAssignments) {
	    List<OrganizationalEntity> potentialOwners = peopleAssignments.getPotentialOwners();
	    
	    Object groupIds = adjustParam(workItem.getParameter(GROUP_ID), groupFilter);
        
	    if (groupIds instanceof Collection) {
            processPeopleAssignments((Collection<OrganizationalEntity>)groupIds, potentialOwners);
        } else {
            processPeopleAssignments((String)groupIds, potentialOwners, false);
        }                  
	}
	
	@SuppressWarnings("unchecked")
	protected void assignBusinessAdministrators(WorkItem workItem, PeopleAssignments peopleAssignments) {
	    List<OrganizationalEntity> businessAdministrators = peopleAssignments.getBusinessAdministrators();
	    
	    Object businessAdminGroupIds = adjustParam(workItem.getParameter(BUSINESSADMINISTRATOR_GROUP_ID), groupFilter);
	    Object businessAdministratorIds = adjustParam(workItem.getParameter(BUSINESSADMINISTRATOR_ID), userFilter);
		
        
        if (!hasAdminAssigned(businessAdministrators)) {
            User administrator = TaskModelProvider.getFactory().newUser();
        	((InternalOrganizationalEntity) administrator).setId(administratorUser);        
            businessAdministrators.add(administrator);
            Group adminGroup = TaskModelProvider.getFactory().newGroup();
        	((InternalOrganizationalEntity) adminGroup).setId(administratorGroup);        
            businessAdministrators.add(adminGroup);
        }
        
        if (businessAdministratorIds instanceof Collection) {
            processPeopleAssignments((Collection<OrganizationalEntity>)businessAdministratorIds, businessAdministrators);
        } else {
            processPeopleAssignments((String)businessAdministratorIds, businessAdministrators, true);
        } 
        if (businessAdminGroupIds instanceof Collection) {
            processPeopleAssignments((Collection<OrganizationalEntity>)businessAdminGroupIds, businessAdministrators);
        } else {
            processPeopleAssignments((String)businessAdminGroupIds, businessAdministrators, false);
        }         
	}
	
	@SuppressWarnings("unchecked")
    protected void assignTaskStakeholders(WorkItem workItem, InternalPeopleAssignments peopleAssignments) {
	    List<OrganizationalEntity> taskStakeholders = peopleAssignments.getTaskStakeholders();
	    Object taskStakehodlerIds = adjustParam(workItem.getParameter(TASKSTAKEHOLDER_ID), noFilter);
		
	    if (taskStakehodlerIds instanceof Collection) {
            processPeopleAssignments((Collection<OrganizationalEntity>)taskStakehodlerIds, taskStakeholders);
        } else {
            processPeopleAssignments((String)taskStakehodlerIds, taskStakeholders, true);
        }		
	}

	@SuppressWarnings("unchecked")
    protected void assignExcludedOwners(WorkItem workItem, InternalPeopleAssignments peopleAssignments) {
        List<OrganizationalEntity> excludedOwners = peopleAssignments.getExcludedOwners();
        Object excludedOwnerIds = adjustParam(workItem.getParameter(EXCLUDED_OWNER_ID), userFilter);
        
        if (excludedOwnerIds instanceof Collection) {
            processPeopleAssignments((Collection<OrganizationalEntity>)excludedOwnerIds, excludedOwners);
        } else {
            processPeopleAssignments((String)excludedOwnerIds, excludedOwners, true);
        }
        
    }

    @SuppressWarnings("unchecked")
    protected void assignRecipients(WorkItem workItem, InternalPeopleAssignments peopleAssignments) {
        List<OrganizationalEntity> recipients = peopleAssignments.getRecipients();
        
        Object recipientIds = adjustParam(workItem.getParameter(RECIPIENT_ID), noFilter);
        
        if (recipientIds instanceof Collection) {
            processPeopleAssignments((Collection<OrganizationalEntity>)recipientIds, recipients);
        } else {
            processPeopleAssignments((String)recipientIds, recipients, true);
        }

    }

	protected void processPeopleAssignments(String peopleAssignmentIds, List<OrganizationalEntity> organizationalEntities, boolean user) {

        if (peopleAssignmentIds != null && peopleAssignmentIds.trim().length() > 0) {

            String[] ids = peopleAssignmentIds.split(separator);
            for (String id : ids) {
                id = id.trim();
                // JBPM-7356 - ignore empty strings
                if(id.length()==0)
                    continue;
                boolean exists = false;
                for (OrganizationalEntity orgEntity : organizationalEntities) {
                    if (orgEntity.getId().equals(id)) {
                        exists = true;
                    }
                }

                if (!exists) {
                    OrganizationalEntity organizationalEntity = null;
                    if (user) {
                    	organizationalEntity = TaskModelProvider.getFactory().newUser();
                    	((InternalOrganizationalEntity) organizationalEntity).setId(id);
                        
                    } else {
                    	organizationalEntity = TaskModelProvider.getFactory().newGroup();
                    	((InternalOrganizationalEntity) organizationalEntity).setId(id);
                    }
                    organizationalEntities.add(organizationalEntity);

                }
            }
        }
	}
	
   protected void processPeopleAssignments(Collection<OrganizationalEntity> peopleAssignmentIds, List<OrganizationalEntity> organizationalEntities) {

        if (peopleAssignmentIds != null) {            
            for (OrganizationalEntity entity : peopleAssignmentIds) {
                
                boolean exists = organizationalEntities.contains(entity);
                if (!exists) {                
                    organizationalEntities.add(entity);
                }
            }
        }
    }
	
	protected InternalPeopleAssignments getNullSafePeopleAssignments(Task task) {
		
		InternalPeopleAssignments peopleAssignments = (InternalPeopleAssignments) task.getPeopleAssignments();
        
        if (peopleAssignments == null) {
        	
        	peopleAssignments = (InternalPeopleAssignments) TaskModelProvider.getFactory().newPeopleAssignments();
        	peopleAssignments.setPotentialOwners(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setBusinessAdministrators(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setExcludedOwners(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setRecipients(new ArrayList<OrganizationalEntity>());
        	peopleAssignments.setTaskStakeholders(new ArrayList<OrganizationalEntity>());

        }
        
		return peopleAssignments;
		
	}
	
	protected boolean hasAdminAssigned(Collection<OrganizationalEntity> businessAdmins) {
	    for (OrganizationalEntity entity : businessAdmins) {
	        if (administratorUser.equals(entity.getId()) || administratorGroup.equals(entity.getId())) {
	            return true;
	        }
	    }
	    return false;
	}
	
    protected Object adjustParam(Object currentValue, Predicate<? super OrganizationalEntity> filter) {
        if (currentValue == null || caseFile == null) {
            return currentValue;
        }
        try {
            
            if (caseFile instanceof CaseAssignment) {
                Collection<OrganizationalEntity> foundMatchingAssignment = new ArrayList<>();
                String[] ids = currentValue.toString().split(separator);
                for (String id : ids) {
                    Collection<OrganizationalEntity> tmp = ((CaseAssignment) caseFile).getAssignments(id)
                            .stream()
                            .filter(filter)
                            .collect(Collectors.toList());
                    
                    if (tmp.isEmpty()) {
                        throw new RuntimeException("Case role '" + id + "' has no matching assignments");
                    }
                    
                    foundMatchingAssignment.addAll(tmp);
                }
                return foundMatchingAssignment;
            }
            
        } catch (IllegalArgumentException e) {
            // no role found with given name
        }
        
        return currentValue;
    }
	
}

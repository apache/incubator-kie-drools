package org.jbpm.services.task.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.drools.core.util.StringUtils;
import org.jbpm.services.task.exception.CannotAddTaskException;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.Group;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.OrganizationalEntity;
import org.kie.internal.task.api.model.PeopleAssignments;
import org.kie.internal.task.api.model.Reassignment;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.TaskData;
import org.kie.internal.task.api.model.User;

public class AbstractUserGroupCallbackDecorator {

    @Inject 
    private JbpmServicesPersistenceManager pm;
    @Inject
    private UserGroupCallback userGroupCallback;
    private Map<String, Boolean> userGroupsMap = new HashMap<String, Boolean>();

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }
    
    
    protected List<String> doUserGroupCallbackOperation(String userId, List<String> groupIds) {

        doCallbackUserOperation(userId);
        doCallbackGroupsOperation(userId, groupIds);
        List<String> allGroupIds = null;

        return userGroupCallback.getGroupsForUser(userId, groupIds, allGroupIds);

    }

    protected boolean doCallbackUserOperation(String userId) {

        if (userId != null && userGroupCallback.existsUser(userId)) {
            addUserFromCallbackOperation(userId);
            return true;
        }
        return false;

    }

    protected boolean doCallbackGroupOperation(String groupId) {

        if (groupId != null && userGroupCallback.existsGroup(groupId)) {
            addGroupFromCallbackOperation(groupId);
            return true;
        }
        return false;

    }

    protected void addUserFromCallbackOperation(String userId) {
        try {
            boolean userExists = pm.find(UserImpl.class, userId) != null;
            if (!StringUtils.isEmpty(userId) && !userExists) {
                UserImpl user = new UserImpl(userId);
                pm.persist(user);
            }
        } catch (Throwable t) {
            //logger.log(Level.SEVERE, "Unable to add user " + userId);
        }
    }

    protected void doCallbackGroupsOperation(String userId, List<String> groupIds) {

        if (userId != null) {

            if (groupIds != null && groupIds.size() > 0) {

                List<String> userGroups = userGroupCallback.getGroupsForUser(userId, groupIds, null);
                for (String groupId : groupIds) {

                    if (userGroupCallback.existsGroup(groupId) && userGroups != null && userGroups.contains(groupId)) {
                        addGroupFromCallbackOperation(groupId);
                    }
                }
            } else {
                if (!(userGroupsMap.containsKey(userId) && userGroupsMap.get(userId).booleanValue())) {
                    List<String> userGroups = userGroupCallback.getGroupsForUser(userId, null, null);
                    if (userGroups != null && userGroups.size() > 0) {
                        for (String group : userGroups) {
                            addGroupFromCallbackOperation(group);
                        }
                        userGroupsMap.put(userId, true);
                    }
                }
            }
        } else {
            if (groupIds != null) {
                for (String groupId : groupIds) {
                    addGroupFromCallbackOperation(groupId);
                }
            }
        }

    }

    protected void addGroupFromCallbackOperation(String groupId) {
        try {
            boolean groupExists = pm.find(GroupImpl.class, groupId) != null;
            if (!StringUtils.isEmpty(groupId) && !groupExists) {
                GroupImpl group = new GroupImpl(groupId);
                pm.persist(group);
            }
        } catch (Throwable t) {
            //logger.log(Level.WARNING, "UserGroupCallback has not been registered.");
        }
    }

    protected void doCallbackOperationForTaskData(TaskData data) {

        if (data.getActualOwner() != null) {
            boolean userExists = doCallbackUserOperation(data.getActualOwner().getId());
            if (!userExists) {
                // remove it from the task to avoid foreign key constraint exception
                data.setActualOwner(null);
                data.setStatus(Status.Ready);
            }
        }

        if (data.getCreatedBy() != null) {
            boolean userExists = doCallbackUserOperation(data.getCreatedBy().getId());
            if (!userExists) {
                // remove it from the task to avoid foreign key constraint exception
                data.setCreatedBy(null);
            }
        }

    }

    protected void doCallbackOperationForPotentialOwners(List<OrganizationalEntity> potentialOwners) {

        List<OrganizationalEntity> nonExistingEntities = new ArrayList<OrganizationalEntity>();

        for (OrganizationalEntity orgEntity : potentialOwners) {
            if (orgEntity instanceof User) {
                boolean userExists = doCallbackUserOperation(orgEntity.getId());
                if (!userExists) {
                    nonExistingEntities.add(orgEntity);
                }
            }
            if (orgEntity instanceof Group) {
                boolean groupExists = doCallbackGroupOperation(orgEntity.getId());
                if (!groupExists) {
                    nonExistingEntities.add(orgEntity);
                }
            }
        }
        if (!nonExistingEntities.isEmpty()) {
            potentialOwners.removeAll(nonExistingEntities);
        }

    }

    protected void doCallbackOperationForPeopleAssignments(PeopleAssignments assignments) {

        List<OrganizationalEntity> nonExistingEntities = new ArrayList<OrganizationalEntity>();

        if (assignments != null) {
            List<? extends OrganizationalEntity> businessAdmins = assignments.getBusinessAdministrators();
            if (businessAdmins != null) {
                for (OrganizationalEntity admin : businessAdmins) {
                    if (admin instanceof UserImpl) {
                        boolean userExists = doCallbackUserOperation(admin.getId());
                        if (!userExists) {
                            nonExistingEntities.add(admin);
                        }
                    }
                    if (admin instanceof GroupImpl) {
                        boolean groupExists = doCallbackGroupOperation(admin.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(admin);
                        }
                    }
                }

                if (!nonExistingEntities.isEmpty()) {
                    businessAdmins.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }

            if (businessAdmins == null || businessAdmins.isEmpty()) {
                // throw an exception as it should not be allowed to create task without administrator
                throw new CannotAddTaskException("There are no known Business Administrators, task cannot be created according to WS-HT specification");
            }

            List<? extends OrganizationalEntity> potentialOwners = assignments.getPotentialOwners();
            if (potentialOwners != null) {
                for (OrganizationalEntity powner : potentialOwners) {
                    if (powner instanceof User) {
                        boolean userExists = doCallbackUserOperation(powner.getId());
                        if (!userExists) {
                            nonExistingEntities.add(powner);
                        }
                    }
                    if (powner instanceof Group) {
                        boolean groupExists = doCallbackGroupOperation(powner.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(powner);
                        }
                    }
                }
                if (!nonExistingEntities.isEmpty()) {
                    potentialOwners.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }

            if (assignments.getTaskInitiator() != null && assignments.getTaskInitiator().getId() != null) {
                doCallbackUserOperation(assignments.getTaskInitiator().getId());
            }

            List<? extends OrganizationalEntity> excludedOwners = assignments.getExcludedOwners();
            if (excludedOwners != null) {
                for (OrganizationalEntity exowner : excludedOwners) {
                    if (exowner instanceof User) {
                        boolean userExists = doCallbackUserOperation(exowner.getId());
                        if (!userExists) {
                            nonExistingEntities.add(exowner);
                        }
                    }
                    if (exowner instanceof Group) {
                        boolean groupExists = doCallbackGroupOperation(exowner.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(exowner);
                        }
                    }
                }
                if (!nonExistingEntities.isEmpty()) {
                    excludedOwners.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }

            List<? extends OrganizationalEntity> recipients = assignments.getRecipients();
            if (recipients != null) {
                for (OrganizationalEntity recipient : recipients) {
                    if (recipient instanceof User) {
                        boolean userExists = doCallbackUserOperation(recipient.getId());
                        if (!userExists) {
                            nonExistingEntities.add(recipient);
                        }
                    }
                    if (recipient instanceof GroupImpl) {
                        boolean groupExists = doCallbackGroupOperation(recipient.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(recipient);
                        }
                    }
                }
                if (!nonExistingEntities.isEmpty()) {
                    recipients.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }

            List<? extends OrganizationalEntity> stakeholders = assignments.getTaskStakeholders();
            if (stakeholders != null) {
                for (OrganizationalEntity stakeholder : stakeholders) {
                    if (stakeholder instanceof User) {
                        boolean userExists = doCallbackUserOperation(stakeholder.getId());
                        if (!userExists) {
                            nonExistingEntities.add(stakeholder);
                        }
                    }
                    if (stakeholder instanceof Group) {
                        boolean groupExists = doCallbackGroupOperation(stakeholder.getId());
                        if (!groupExists) {
                            nonExistingEntities.add(stakeholder);
                        }
                    }
                }
                if (!nonExistingEntities.isEmpty()) {
                    stakeholders.removeAll(nonExistingEntities);
                    nonExistingEntities.clear();
                }
            }
        }


    }
     protected void doCallbackOperationForTaskDeadlines(Deadlines deadlines) {
        if(deadlines != null) {
            if(deadlines.getStartDeadlines() != null) {
                List<? extends Deadline> startDeadlines = deadlines.getStartDeadlines();
                for(Deadline startDeadline : startDeadlines) {
                    List<? extends Escalation> escalations = startDeadline.getEscalations();
                    if(escalations != null) {
                        for(Escalation escalation : escalations) {
                            List<? extends Notification> notifications = escalation.getNotifications();
                            List<? extends Reassignment> ressignments = escalation.getReassignments();
                            if(notifications != null) {
                                for(Notification notification : notifications) {
                                    List<? extends OrganizationalEntity> recipients = notification.getRecipients();
                                    if(recipients != null) {
                                        for(OrganizationalEntity recipient : recipients) {
                                            if(recipient instanceof User) {
                                                doCallbackUserOperation(recipient.getId());
                                            }
                                            if(recipient instanceof Group) {
                                                doCallbackGroupOperation(recipient.getId());
                                            }
                                        }
                                    }
                                    List<? extends OrganizationalEntity> administrators = notification.getBusinessAdministrators();
                                    if(administrators != null) {
                                        for(OrganizationalEntity administrator : administrators) {
                                            if(administrator instanceof User) {
                                                doCallbackUserOperation(administrator.getId());
                                            }
                                            if(administrator instanceof Group) {
                                                doCallbackGroupOperation(administrator.getId());
                                            }
                                        }
                                    }
                                }
                            }
                            if(ressignments != null) {
                                for(Reassignment reassignment : ressignments) {
                                    List<? extends OrganizationalEntity> potentialOwners = reassignment.getPotentialOwners();
                                    if(potentialOwners != null) {
                                        for(OrganizationalEntity potentialOwner : potentialOwners) {
                                            if(potentialOwner instanceof User) {
                                                doCallbackUserOperation(potentialOwner.getId());
                                            }
                                            if(potentialOwner instanceof Group) {
                                                doCallbackGroupOperation(potentialOwner.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if(deadlines.getEndDeadlines() != null) {
                List<? extends Deadline> endDeadlines = deadlines.getEndDeadlines();
                for(Deadline endDeadline : endDeadlines) {
                    List<? extends Escalation> escalations = endDeadline.getEscalations();
                    if(escalations != null) {
                        for(Escalation escalation : escalations) {
                            List<? extends Notification> notifications = escalation.getNotifications();
                            List<? extends Reassignment> ressignments = escalation.getReassignments();
                            if(notifications != null) {
                                for(Notification notification : notifications) {
                                    List<? extends OrganizationalEntity> recipients = notification.getRecipients();
                                    if(recipients != null) {
                                        for(OrganizationalEntity recipient : recipients) {
                                            if(recipient instanceof User) {
                                                doCallbackUserOperation(recipient.getId());
                                            }
                                            if(recipient instanceof Group) {
                                                doCallbackGroupOperation(recipient.getId());
                                            }
                                        }
                                    }
                                    List<? extends OrganizationalEntity> administrators = notification.getBusinessAdministrators();
                                    if(administrators != null) {
                                        for(OrganizationalEntity administrator : administrators) {
                                            if(administrator instanceof User) {
                                                doCallbackUserOperation(administrator.getId());
                                            }
                                            if(administrator instanceof Group) {
                                                doCallbackGroupOperation(administrator.getId());
                                            }
                                        }
                                    }
                                }
                            }
                            if(ressignments != null) {
                                for(Reassignment reassignment : ressignments) {
                                    List<? extends OrganizationalEntity> potentialOwners = reassignment.getPotentialOwners();
                                    if(potentialOwners != null) {
                                        for(OrganizationalEntity potentialOwner : potentialOwners) {
                                            if(potentialOwner instanceof User) {
                                                doCallbackUserOperation(potentialOwner.getId());
                                            }
                                            if(potentialOwner instanceof Group) {
                                                doCallbackGroupOperation(potentialOwner.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

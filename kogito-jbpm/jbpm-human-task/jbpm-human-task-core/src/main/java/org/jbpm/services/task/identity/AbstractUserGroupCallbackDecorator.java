package org.jbpm.services.task.identity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.drools.core.util.StringUtils;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.services.task.exception.CannotAddTaskException;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.Escalation;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.model.Notification;
import org.kie.internal.task.api.model.Reassignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractUserGroupCallbackDecorator {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractUserGroupCallbackDecorator.class);
    
    private static ReentrantLock lock = new ReentrantLock();
    private volatile static Set<String> localcache = new CopyOnWriteArraySet<String>();

    @Inject 
    private JbpmServicesPersistenceManager pm;
    @Inject
    private UserGroupCallback userGroupCallback;
    private Map<String, Boolean> userGroupsMap = new HashMap<String, Boolean>();
    
    private List<String> restrictedGroups = new ArrayList<String>();

    public AbstractUserGroupCallbackDecorator() {
        try {
            InputStream in = this.getClass().getResourceAsStream("/restricted-groups.properties");
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                
                restrictedGroups.addAll(props.stringPropertyNames());
            }
        } catch (Exception e) {
            logger.warn("Error when loading restricted groups for human task service {}", e.getMessage());
        }
    }
    
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

        return filterGroups(userGroupCallback.getGroupsForUser(userId, groupIds, allGroupIds));

    }

    protected boolean doCallbackUserOperation(String userId) {

        if (userId != null && userGroupCallback.existsUser(userId)) {
            addUserFromCallbackOperation(userId);
            return true;
        }
        return false;

    }

    protected boolean doCallbackGroupOperation(String groupId) {

        if (groupId != null && userGroupCallback.existsGroup(groupId) && !restrictedGroups.contains(groupId)) {
            addGroupFromCallbackOperation(groupId);
            return true;
        }
        return false;

    }

    protected void addUserFromCallbackOperation(String userId) {
    	UserImpl user = pm.find(UserImpl.class, userId);
        boolean userExists = user != null;
        if (!StringUtils.isEmpty(userId) && !userExists) {
            user = new UserImpl(userId);
            persistIfNotExists(user);
        } 
    }
    
    protected void persistIfNotExists(final OrganizationalEntity entity) {
    	boolean exists = localcache.contains(entity.getId());
    	if (exists) {
    		logger.debug("No need to lock {}", Thread.currentThread().getName());
    		return;
    	}
    	
    	logger.debug("About to lock {}", Thread.currentThread().getName());
    	lock.lock();
    	logger.debug("Lock accuried {}" + Thread.currentThread().getName());
		try {
			exists = localcache.contains(entity.getId());
	    	logger.debug("Entity {} exists {} thread {}", entity, exists, Thread.currentThread().getName());
	        if (!StringUtils.isEmpty(entity.getId()) && !exists) {
	            pm.persist(entity);
	            
	            logger.debug("Persisted {} by thread {}", entity, Thread.currentThread().getName());
	        }
    	} finally {
    		if (exists) {
    			logger.debug("Unlock directly {}", Thread.currentThread().getName());
    			// unlock directly when exists in local cache
    			lock.unlock();
    		} else {
    			logger.debug("Unlock on transaction completion {}", Thread.currentThread().getName());
    			
    			JtaTransactionManager tm = new JtaTransactionManager(null, null, null);
    			if (tm.getStatus() != JtaTransactionManager.STATUS_NO_TRANSACTION
    	                && tm.getStatus() != JtaTransactionManager.STATUS_ROLLEDBACK
    	                && tm.getStatus() != JtaTransactionManager.STATUS_COMMITTED) {
	    			// unlock after transaction was completed
		    		
		    		tm.registerTransactionSynchronization(new TransactionSynchronization() {
						
						@Override
						public void beforeCompletion() {					
						}
						
						@Override
						public void afterCompletion(int status) {
							if (lock.hasQueuedThreads()) {
								localcache.add(entity.getId());
							}
							lock.unlock();	
							logger.debug("Unlocked {}", Thread.currentThread().getName());
						}
					});
    			} else {
    				logger.debug("Unlock directly no tx sync avaliable {}", Thread.currentThread().getName());
        			// unlock directly when exists in local cache
        			lock.unlock();
    			}
    		}
    	}
    }

    protected void doCallbackGroupsOperation(String userId, List<String> groupIds) {

        if (userId != null) {

            if (groupIds != null && groupIds.size() > 0) {

                List<String> userGroups = filterGroups(userGroupCallback.getGroupsForUser(userId, groupIds, null));
                for (String groupId : groupIds) {

                    if (userGroupCallback.existsGroup(groupId) && userGroups != null && userGroups.contains(groupId)) {
                        addGroupFromCallbackOperation(groupId);
                    }
                }
            } else {
                if (!(userGroupsMap.containsKey(userId) && userGroupsMap.get(userId).booleanValue())) {
                    List<String> userGroups = filterGroups(userGroupCallback.getGroupsForUser(userId, null, null));
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
    	GroupImpl group = pm.find(GroupImpl.class, groupId);
    	boolean groupExists = group != null;
        if (!StringUtils.isEmpty(groupId) && !groupExists) {
            group = new GroupImpl(groupId);
            persistIfNotExists(group);
        }    
    }

    protected void doCallbackOperationForTaskData(InternalTaskData data) {

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

    protected void doCallbackOperationForPeopleAssignments(InternalPeopleAssignments assignments) {

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
     
     protected List<String> filterGroups(List<String> groups) {
         if (groups != null) {
             groups.removeAll(restrictedGroups);
         }
         
         return groups;
     }
}

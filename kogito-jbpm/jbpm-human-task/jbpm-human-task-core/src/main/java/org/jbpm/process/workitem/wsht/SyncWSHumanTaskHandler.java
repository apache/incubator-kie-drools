/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.workitem.wsht;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.SubTasksStrategyFactory;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.TaskService;
import org.jbpm.task.User;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.PermissionDeniedException;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.task.utils.OnErrorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 
 */
@Deprecated
public class SyncWSHumanTaskHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(SyncWSHumanTaskHandler.class);
    
    private String ipAddress = "127.0.0.1";
    private int port = 9123;
    
    private TaskService client;
    private WorkItemManager manager = null;
    private KnowledgeRuntime session;
    private boolean local = false;
    private OnErrorAction action;
    private boolean initialized = false;
    private Map<TaskEventKey, EventResponseHandler> eventHandlers = new HashMap<TaskEventKey, EventResponseHandler>();
    
    public SyncWSHumanTaskHandler() {
    	this.action = OnErrorAction.LOG;
    }

    public SyncWSHumanTaskHandler(TaskService client) {
        this.client = client;
        this.action = OnErrorAction.LOG;
    }
    
    public SyncWSHumanTaskHandler(TaskService client, KnowledgeRuntime session) {
        this.client = client;
        this.session = session;
        this.action = OnErrorAction.LOG;
    }
    
    public SyncWSHumanTaskHandler(TaskService client, OnErrorAction action) {
        this.client = client;
        this.action = action;
    }
    
    public SyncWSHumanTaskHandler(TaskService client, KnowledgeRuntime session, OnErrorAction action) {
        this.client = client;
        this.session = session;
        this.action = action;
    }
    
    public void setConnection(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void setClient(TaskService client) {
        this.client = client;
    }
    
    public void setAction(OnErrorAction action) {
		this.action = action;
	}

    public void connect() {
    	if (!initialized) {
	        if (client == null) {
	            throw new IllegalStateException("You must set the client to the work item to work");
	        }
	        if (client != null) {
	            boolean connected = client.connect(ipAddress, port);
	            if (!connected) {
	                throw new IllegalArgumentException("Could not connect task client");
	            }
	        }
	        registerTaskEvents();
	        initialized = true;
    	}
    }
    
    public void setLocal(boolean local) {
    	this.local = local;
    }

    private void registerTaskEvents() {
        TaskCompletedHandler eventResponseHandler = new TaskCompletedHandler();
        TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskFailedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskSkippedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
    }

    public void setManager(WorkItemManager manager) {
        this.manager = manager;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (this.session == null) {
	    	if (this.manager == null) {
	            this.manager = manager;
	        } else {
	            if (this.manager != manager) {
	                throw new IllegalArgumentException(
	                        "This WSHumanTaskHandler can only be used for one WorkItemManager");
	            }
	        }
        }
        connect();
        Task task = new Task();
        String taskName = (String) workItem.getParameter("TaskName"); 
        if (taskName != null) {
            List<I18NText> names = new ArrayList<I18NText>();
            names.add(new I18NText("en-UK", taskName));
            task.setNames(names);
        }
        String comment = (String) workItem.getParameter("Comment");
        if (comment == null) {
            comment = "";
        }
        List<I18NText> descriptions = new ArrayList<I18NText>();
        descriptions.add(new I18NText("en-UK", comment));
        task.setDescriptions(descriptions);
        List<I18NText> subjects = new ArrayList<I18NText>();
        subjects.add(new I18NText("en-UK", comment));
        task.setSubjects(subjects);
        
        String priorityString = (String) workItem.getParameter("Priority");
        int priority = 0;
        if (priorityString != null) {
            try {
                priority = new Integer(priorityString);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        task.setPriority(priority);

        TaskData taskData = new TaskData();
        taskData.setWorkItemId(workItem.getId());
        taskData.setProcessInstanceId(workItem.getProcessInstanceId());
        if(session != null && session.getProcessInstance(workItem.getProcessInstanceId()) != null) {
			taskData.setProcessId(session.getProcessInstance(workItem.getProcessInstanceId()).getProcess().getId());
		}
        if(session != null && (session instanceof StatefulKnowledgeSession)) { 
        	taskData.setProcessSessionId( ((StatefulKnowledgeSession) session).getId() );
        }
        taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if (parentId != null) {
            taskData.setParentId(parentId);
        }

        String subTaskStrategiesCommaSeparated = (String) workItem.getParameter("SubTaskStrategies");
        if (subTaskStrategiesCommaSeparated != null && !subTaskStrategiesCommaSeparated.equals("")) {
            String[] subTaskStrategies = subTaskStrategiesCommaSeparated.split(",");
            List<SubTasksStrategy> strategies = new ArrayList<SubTasksStrategy>();
            for (String subTaskStrategyString : subTaskStrategies) {
                SubTasksStrategy subTaskStrategy = SubTasksStrategyFactory.newStrategy(subTaskStrategyString);
                strategies.add(subTaskStrategy);
            }
            task.setSubTaskStrategies(strategies);
        }

        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();

        String actorId = (String) workItem.getParameter("ActorId");
        if (actorId != null && actorId.trim().length() > 0) {
            String[] actorIds = actorId.split(",");
            for (String id : actorIds) {
                potentialOwners.add(new User(id.trim()));
            }
            //Set the first user as creator ID??? hmmm might be wrong
            if (potentialOwners.size() > 0) {
                taskData.setCreatedBy((User) potentialOwners.get(0));
            }
        }

        String groupId = (String) workItem.getParameter("GroupId");
        if (groupId != null && groupId.trim().length() > 0) {
            String[] groupIds = groupId.split(",");
            for (String id : groupIds) {
                potentialOwners.add(new Group(id.trim()));
            }
        }

        assignments.setPotentialOwners(potentialOwners);
        List<OrganizationalEntity> businessAdministrators = new ArrayList<OrganizationalEntity>();
        businessAdministrators.add(new User("Administrator"));
        assignments.setBusinessAdministrators(businessAdministrators);
        task.setPeopleAssignments(assignments);

        task.setTaskData(taskData);

        ContentData content = null;
        Object contentObject = workItem.getParameter("Content");
        if (contentObject == null) {
            contentObject = workItem.getParameters();
        }
        if (contentObject != null) {
            content = ContentMarshallerHelper.marshal(contentObject,  session.getEnvironment());
        }
        task.setDeadlines(HumanTaskHandlerHelper.setDeadlines(workItem, businessAdministrators));
        try {
        	client.addTask(task, content);
        	
        } catch (Exception e) {
        	
        	if (action.equals(OnErrorAction.ABORT)) {
				session.getWorkItemManager().abortWorkItem(workItem.getId());
				
			} else if (action.equals(OnErrorAction.RETHROW)) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				} else {
					throw new RuntimeException(e);
				}
				
			} else if (action.equals(OnErrorAction.LOG)) {
				StringBuffer logMsg = new StringBuffer();
				logMsg.append(new Date() + ": Error when creating task on task server for work item id " + workItem.getId());
				logMsg.append(". Error reported by task server: " + e.getMessage() ); 
                logger.error(logMsg.toString(), e);
			}
		}
    }

    public void dispose() throws Exception {
        for(TaskEventKey key : eventHandlers.keySet()){
            client.registerForEvent(key, true, eventHandlers.get(key));
        }
        eventHandlers.clear();
        if (client != null) {
            client.disconnect();
        }
        
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        Task task = client.getTaskByWorkItemId(workItem.getId());
        if (task != null) {
            try {
                client.exit(task.getId(), "Administrator");
            } catch (PermissionDeniedException e) {
                logger.info(e.getMessage());
            }
        }
    }

    private class TaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {
        
        public void execute(Payload payload) {
            TaskEvent event = ( TaskEvent ) payload.get();
        	final long taskId = event.getTaskId();
        	if (local) {
        		handleCompletedTask(taskId);
        	} else {
	        	Runnable runnable = new Runnable() {
	        		public void run() {
	        			handleCompletedTask(taskId);
	        		}
	        	};
	        	new Thread(runnable).start();
        	}
        }
        
        public boolean isRemove() {
        	return false;
        }
        
        public void handleCompletedTask(long taskId) {
        	Task task = client.getTask(taskId);
			long workItemId = task.getTaskData().getWorkItemId();
			if (task.getTaskData().getStatus() == Status.Completed) {
				String userId = task.getTaskData().getActualOwner().getId();
				Map<String, Object> results = new HashMap<String, Object>();
				results.put("ActorId", userId);
				long contentId = task.getTaskData().getOutputContentId();
				if (contentId != -1) {
					Content content = client.getContent(contentId);
                                        Object result = ContentMarshallerHelper.unmarshall(content.getContent(), session.getEnvironment());
					results.put("Result", result);
                                        if (result instanceof Map) {
                                            Map<?, ?> map = (Map) result;
                                            for (Map.Entry<?, ?> entry: map.entrySet()) {
						if (entry.getKey() instanceof String) {
                                                    results.put((String) entry.getKey(), entry.getValue());
						}
                                            }
					}
					if (session != null) {
                                            session.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
					} else {
                                            manager.completeWorkItem(task.getTaskData().getWorkItemId(), results);
					}
					
				} else {
					if (session != null) {
						session.getWorkItemManager().completeWorkItem(workItemId, results);
					} else {
						manager.completeWorkItem(workItemId, results);
					}
				}
			} else {
				if (session != null) {
					session.getWorkItemManager().abortWorkItem(workItemId);
				} else {
					manager.abortWorkItem(workItemId);
				}
			}

        }
    }
}

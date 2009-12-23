package org.drools.vsm.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.eventmessaging.EventKey;
import org.drools.eventmessaging.Payload;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.task.AccessType;
import org.drools.task.Content;
import org.drools.task.Group;
import org.drools.task.I18NText;
import org.drools.task.OrganizationalEntity;
import org.drools.task.PeopleAssignments;
import org.drools.task.SubTasksStrategy;
import org.drools.task.SubTasksStrategyFactory;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.drools.task.event.TaskCompletedEvent;
import org.drools.task.event.TaskEvent;
import org.drools.task.event.TaskEventKey;
import org.drools.task.event.TaskFailedEvent;
import org.drools.task.event.TaskSkippedEvent;
import org.drools.task.service.Command;
import org.drools.task.service.ContentData;
import org.drools.task.service.HumanTaskServiceImpl;
import org.drools.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.drools.vsm.GenericConnector;
import org.drools.vsm.Message;
import org.drools.vsm.mina.MinaConnector;
import org.drools.vsm.mina.MinaIoHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.AddTaskMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.GetContentMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.GetTaskMessageResponseHandler;
import org.drools.vsm.task.eventmessaging.EventMessageResponseHandler;


public class CommandBasedVSMWSHumanTaskHandler implements WorkItemHandler {

	private String ipAddress = "127.0.0.1";
	private int port = 9124;
	private SocketAddress address;
	private GenericConnector connector;
	private HumanTaskServiceImpl client;
	private KnowledgeRuntime session;
	private Map<Long, Long> idMapping = new HashMap<Long, Long>();
	private Map<Long, WorkItemManager> managers = new HashMap<Long, WorkItemManager>();
	
	public CommandBasedVSMWSHumanTaskHandler(KnowledgeRuntime session) {
		this.session = session;
		this.address = new InetSocketAddress(ipAddress, port);
	}

	public void connect() {
		if (connector == null) {
			NioSocketConnector htclientConnector = new NioSocketConnector();
			htclientConnector.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener()));
			connector = new MinaConnector( "client ht",
											htclientConnector,
											this.address,
											SystemEventListenerFactory.getSystemEventListener() );
            boolean connected = connector.connect();
			if (!connected)
				throw new IllegalArgumentException("Could not connect task client");
			int id = ((StatefulKnowledgeSession)session).getId();
			client = new HumanTaskServiceImpl(connector, new AtomicInteger(), String.valueOf(id), id);
		}
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		connect();
		Task task = new Task();
		String taskName = (String) workItem.getParameter("TaskName");
		if (taskName != null) {
			List<I18NText> names = new ArrayList<I18NText>();
			names.add(new I18NText("en-UK", taskName));
			task.setNames(names);
		}
		String comment = (String) workItem.getParameter("Comment");
		if (comment != null) {
			List<I18NText> descriptions = new ArrayList<I18NText>();
			descriptions.add(new I18NText("en-UK", comment));
			task.setDescriptions(descriptions);
			List<I18NText> subjects = new ArrayList<I18NText>();
			subjects.add(new I18NText("en-UK", comment));
			task.setSubjects(subjects);
		}
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
		taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if(parentId != null){
            taskData.setParentId(parentId);
        }

        String subTaskStrategiesCommaSeparated = (String) workItem.getParameter("SubTaskStrategies");
        if(subTaskStrategiesCommaSeparated!= null && !subTaskStrategiesCommaSeparated.equals("")){
            String[] subTaskStrategies =  subTaskStrategiesCommaSeparated.split(",");
            List<SubTasksStrategy> strategies = new ArrayList<SubTasksStrategy>();
            for(String subTaskStrategyString : subTaskStrategies){
                SubTasksStrategy subTaskStrategy = SubTasksStrategyFactory.newStrategy(subTaskStrategyString);
                strategies.add(subTaskStrategy);
            }
            task.setSubTaskStrategies(strategies);
        }

        PeopleAssignments assignments = new PeopleAssignments();
		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();

        String actorId = (String) workItem.getParameter("ActorId");
		if (actorId != null) {
			
			String[] actorIds = actorId.split(",");
			for (String id: actorIds) {
				User user = new User();
				user.setId(id.trim());
				potentialOwners.add(user);
			}
            //Set the first user as creator ID??? hmmm might be wrong
            if (potentialOwners.size() > 0){
                taskData.setCreatedBy((User)potentialOwners.get(0));
            }
        }
        String groupId = (String) workItem.getParameter("GroupId");
		if (groupId != null) {
			
			String[] groupIds = groupId.split(",");
			for (String id: groupIds) {

				potentialOwners.add(new Group(id));
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
		if (contentObject != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(contentObject);
				out.close();
				content = new ContentData();
				content.setContent(bos.toByteArray());
				content.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		TaskWorkItemAddTaskMessageResponseHandler taskResponseHandler =	
			new TaskWorkItemAddTaskMessageResponseHandler(workItem.getId(), this.managers, idMapping, manager);
		client.addTask(task, content, taskResponseHandler);
	}
	
	public void dispose() {
		if (connector != null) {
			connector.disconnect();
		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		Long taskId = idMapping.get(workItem.getId());
		if (taskId != null) {
			synchronized (idMapping) {
				idMapping.remove(taskId);
			}
			synchronized (managers) {
				managers.remove(taskId);
			}
			client.skip(taskId, "Administrator", null);
		}
	}
	
    public class TaskWorkItemAddTaskMessageResponseHandler extends AbstractBaseResponseHandler implements AddTaskMessageResponseHandler {
    	
    	private Map<Long, WorkItemManager> managers;
    	private Map<Long, Long> idMapping;
    	private WorkItemManager manager;
    	private long workItemId;
        
    	public TaskWorkItemAddTaskMessageResponseHandler(long workItemId,
        		Map<Long, WorkItemManager> managers,  Map<Long, Long> idMapping,
        		WorkItemManager manager) {
			this.workItemId = workItemId;
            this.managers = managers;
			this.idMapping = idMapping;
            this.manager = manager;
        }
        
        public void execute(long taskId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void receive(Message message) {
        	Command command = (Command) message.getPayload();
        	Long taskId = (Long) command.getArguments().get(0);

        	synchronized ( managers ) {
                managers.put(taskId, this.manager);           
            }
            synchronized ( idMapping ) {
                idMapping.put(workItemId, taskId);           
            }
            System.out.println("Created task " + taskId + " for work item " + workItemId);

            EventKey key = new TaskEventKey(TaskCompletedEvent.class, taskId);
            TaskCompletedMessageHandler eventResponseHandler = new TaskCompletedMessageHandler(workItemId, taskId, managers);
            client.registerForEvent( key, true, eventResponseHandler );
            key = new TaskEventKey(TaskFailedEvent.class, taskId );
            client.registerForEvent( key, true, eventResponseHandler );
            key = new TaskEventKey(TaskSkippedEvent.class, taskId );
            client.registerForEvent( key, true, eventResponseHandler );
        }

       

    }
    
    private class TaskCompletedMessageHandler extends AbstractBaseResponseHandler implements EventMessageResponseHandler {
        private long workItemId;
        private long taskId;
		private final Map<Long, WorkItemManager> managers;
        
        public TaskCompletedMessageHandler(long workItemId, long taskId, Map<Long, WorkItemManager> managers) {
            this.workItemId = workItemId;
            this.taskId = taskId;
			this.managers = managers;
        }

        

       

		public void receive(Message message) {
			Command cmd = (Command) message.getPayload();
			Payload payload = (Payload) cmd.getArguments().get(0);
			TaskEvent event = ( TaskEvent ) payload.get();
			if ( event.getTaskId() != taskId ) {
				// defensive check that should never happen, just here for testing                
				setError(new IllegalStateException("Expected task id and arrived task id do not march"));
				return;
			}
			if (event instanceof TaskCompletedEvent) {
				synchronized ( this.managers ) {
		            WorkItemManager manager = this.managers.get(taskId);
		            if (manager != null) {
		            	GetCompletedTaskMessageResponseHandler getTaskResponseHandler = new GetCompletedTaskMessageResponseHandler(manager);
		            	client.getTask(taskId, getTaskResponseHandler);   
		            }
		        }
			} else {
				synchronized ( this.managers ) {
        			WorkItemManager manager = this.managers.get(taskId);
		            if (manager != null)
		            	manager.abortWorkItem(workItemId);
		        }
			}
		}

        public void execute(Payload payload) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private class GetCompletedTaskMessageResponseHandler extends AbstractBaseResponseHandler implements GetTaskMessageResponseHandler {

        private WorkItemManager manager;

		public GetCompletedTaskMessageResponseHandler(WorkItemManager manager) {
			this.manager = manager;
		}

	

        public void receive(Message message) {
        	Command cmd = (Command) message.getPayload();
            Task task = (Task)cmd.getArguments().get(0);
            long workItemId = task.getTaskData().getWorkItemId();
			String userId = task.getTaskData().getActualOwner().getId();
			Map<String, Object> results = new HashMap<String, Object>();
			results.put("ActorId", userId);
			long contentId = task.getTaskData().getOutputContentId();
			if (contentId != -1) {
				GetResultContentMessageResponseHandler getContentResponseHandler =
					new GetResultContentMessageResponseHandler(manager, task, results);
				client.getContent(contentId, getContentResponseHandler);
			} else {
				manager.completeWorkItem(workItemId, results);
			}
        }

        public void execute(Task task) {
            throw new UnsupportedOperationException("Not supported yet.");
        }


    }
    
    private class GetResultContentMessageResponseHandler extends AbstractBaseResponseHandler implements GetContentMessageResponseHandler {
    	
    	private Task task;
    	private final WorkItemManager manager;
    	private Map<String, Object> results;

    	public GetResultContentMessageResponseHandler(WorkItemManager manager, Task task, Map<String, Object> results) {
    		this.manager = manager;
			this.task = task;
    		this.results = results;
    	}

        

        public void receive(Message message) {
        	Command cmd = (Command)message.getPayload();
            Content content = (Content)cmd.getArguments().get(0);
            ByteArrayInputStream bis = new ByteArrayInputStream(content.getContent());
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(bis);
				Object result = in.readObject();
				in.close();
				results.put("Result", result);
				if (result instanceof Map) {
					Map<?, ?> map = (Map) result;
					for (Map.Entry<?, ?> entry: map.entrySet()) {
						if (entry.getKey() instanceof String) {
							results.put((String) entry.getKey(), entry.getValue());
						}
					}
				}
				manager.completeWorkItem(task.getTaskData().getWorkItemId(), results);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
        }

        public void execute(Content content) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}

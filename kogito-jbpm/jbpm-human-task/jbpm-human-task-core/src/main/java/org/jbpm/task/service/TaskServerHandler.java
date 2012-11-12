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

package org.jbpm.task.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskServiceSession.TransactedOperation;
import org.kie.SystemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskServerHandler {

    private static final Logger logger = LoggerFactory.getLogger(TaskServerHandler.class);
    
	private final TaskService service;
    private final Map<String, SessionWriter> clients;

    /**
     * Listener used for logging
     */
    private final SystemEventListener systemEventListener;

    public TaskServerHandler(TaskService service, SystemEventListener systemEventListener) {
        this.service = service;
        this.clients = new HashMap<String, SessionWriter>();
        this.systemEventListener = systemEventListener;
    }

    public void exceptionCaught(SessionWriter session, Throwable cause) throws Exception {
        systemEventListener.exception("Uncaught exception on Server", cause);
    }

    public void messageReceived(final SessionWriter session, Object message) throws Exception {
        final Command cmd = (Command) message;
        final TaskServiceSession taskSession = service.createSession();
        CommandName response = null;
        
        try {
            systemEventListener.debug("Message receieved on server : " + cmd.getName());
            systemEventListener.debug("Arguments : " + Arrays.toString(cmd.getArguments().toArray()));

            switch (cmd.getName()) {
                case OperationRequest: {
                    // prepare
                    response = CommandName.OperationResponse;
                    Operation operation = (Operation) cmd.getArguments().get(0);

                    systemEventListener.debug("Command receieved on server was operation of type: " + operation);

                    long taskId = (Long) cmd.getArguments().get(1);
                    String userId = (String) cmd.getArguments().get(2);
                    String targetEntityId = null;
                    ContentData data = null;
                    List<String> groupIds = null;
                    if (cmd.getArguments().size() > 3) {
                        targetEntityId = (String) cmd.getArguments().get(3);
                        if (cmd.getArguments().size() > 4) {
                            data = (ContentData) cmd.getArguments().get(4);
                            if (cmd.getArguments().size() > 5) {
                                groupIds = (List<String>) cmd.getArguments().get(5);
                            }
                        }
                    }
                    
                    // execute
                    taskSession.taskOperation(operation, taskId, userId, targetEntityId, data, groupIds);

                    // return 
                    List args = Collections.emptyList();
                    Command resultsCmnd = new Command(cmd.getId(), CommandName.OperationResponse, args);
                    session.write(resultsCmnd);
                    break;
                }
                case ClaimNextAvailableRequest: {
                    // prepare
                    response = CommandName.OperationResponse;
                    

                    systemEventListener.debug("Command receieved on server was operation of type: " + CommandName.ClaimNextAvailableRequest);

                    
                    String userId = (String) cmd.getArguments().get(0);
                    String language = (String) cmd.getArguments().get(1);
                    // execute
                    if(cmd.getArguments().size() == 2){
                        taskSession.claimNextAvailable(userId, language);
                    } else if (cmd.getArguments().size() == 3){
                        List<String> groupIds = (List<String>) cmd.getArguments().get(2);
                        taskSession.claimNextAvailable(userId, groupIds, language );
                    }
                            
                    // return 
                    List args = Collections.emptyList();
                    Command resultsCmnd = new Command(cmd.getId(), response, args);
                    session.write(resultsCmnd);
                    break;
                }    
                case GetTaskRequest: {
                    response = CommandName.GetTaskResponse;
                    final long taskId = (Long) cmd.getArguments().get(0);

                    taskSession.doOperationInTransaction(new TransactedOperation() {
                        public void doOperation() {
                            // execute
                            Task task = taskSession.getTask(taskId);

                            // return
                            List args = Arrays.asList((new Task[] {task}));
                            Command resultsCmnd = new Command(cmd.getId(), CommandName.GetTaskResponse, args);
                            try {
                                session.write(resultsCmnd);
                            } catch(IOException ioe) { 
                                throw new IllegalTaskStateException("Could not serialize Task instance.", ioe); 
                            }
                        }
                    });

                    break;
                }
                case AddTaskRequest: {
                    // prepare
                    response = CommandName.AddTaskResponse;
                    Task task = (Task) cmd.getArguments().get(0);
                    ContentData content = (ContentData) cmd.getArguments().get(1);
                    
                    // execute
                    taskSession.addTask(task, content);

                    // return
                    List args = Arrays.asList((new Long[] {task.getId()}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.AddTaskResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case AddCommentRequest: {
                    // prepare
                    response = CommandName.AddCommentResponse;
                    Comment comment = (Comment) cmd.getArguments().get(1);
                    
                    // execute
                    taskSession.addComment((Long) cmd.getArguments().get(0), comment);

                    // return
                    List args = Arrays.asList((new Long[] {comment.getId()}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.AddCommentResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case DeleteCommentRequest: {
                    // prepare
                    response = CommandName.DeleteCommentResponse;
                    long taskId = (Long) cmd.getArguments().get(0);
                    long commentId = (Long) cmd.getArguments().get(1);
                    
                    // execute
                    taskSession.deleteComment(taskId, commentId);

                    // return
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.DeleteCommentResponse,
                            Collections.emptyList());
                    session.write(resultsCmnd);
                    break;
                }
                case AddAttachmentRequest: {
                    // prepare
                    response = CommandName.AddAttachmentResponse;
                    Attachment attachment = (Attachment) cmd.getArguments().get(1);
                    Content content = (Content) cmd.getArguments().get(2);
                    
                    // execute
                    taskSession.addAttachment((Long) cmd.getArguments().get(0), attachment, content);

                    // return
                    List args = Arrays.asList((new Long[] {attachment.getId(), content.getId()}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.AddAttachmentResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case DeleteAttachmentRequest: {
                    // prepare
                    response = CommandName.DeleteAttachmentResponse;
                    long taskId = (Long) cmd.getArguments().get(0);
                    long attachmentId = (Long) cmd.getArguments().get(1);
                    long contentId = (Long) cmd.getArguments().get(2);
                    
                    // execute
                    taskSession.deleteAttachment(taskId, attachmentId, contentId);
                    
                    // return
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.DeleteAttachmentResponse,
                            Collections.emptyList());
                    session.write(resultsCmnd);
                    break;
                }
                case SetDocumentContentRequest: {
                    // prepare
                    response = CommandName.SetDocumentContentResponse;
                    long taskId = (Long) cmd.getArguments().get(0);
                    Content content = (Content) cmd.getArguments().get(1);
                    
                    // execute
                    taskSession.setDocumentContent(taskId,
                            content);

                    // return
                    List args = Arrays.asList((new Long[] {content.getId()}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.SetDocumentContentResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case GetContentRequest: {
                    // prepare
                    response = CommandName.GetContentResponse;
                    final long contentId = (Long) cmd.getArguments().get(0);
                    
                    taskSession.doOperationInTransaction(new TransactedOperation() {
                        public void doOperation() throws Exception {
                    // execute
                    Content content = taskSession.getContent(contentId);

                    // return
                    List args = Arrays.asList((new Content[] {content}));
                            Command resultsCmnd = new Command(cmd.getId(), CommandName.GetContentResponse, args);
                    session.write(resultsCmnd);
                        }
                    });
                    
                    break;
                }
                case QueryTaskByWorkItemId: {
                    // prepare
                    response = CommandName.QueryTaskByWorkItemIdResponse;
                    
                    final long taskId = (Long) cmd.getArguments().get(0);

                    taskSession.doOperationInTransaction(new TransactedOperation() {
                        public void doOperation() {
                            // execute
                            Task result = taskSession.getTaskByWorkItemId((Long) cmd.getArguments().get(0));

                            // return
                            List args = Arrays.asList((new Task[] {result}));
                            Command resultsCmnd = new Command(cmd.getId(), CommandName.QueryTaskByWorkItemIdResponse, args);
                            try {
                                session.write(resultsCmnd);
                            } catch(IOException ioe) { 
                                throw new IllegalTaskStateException("Could not serialize Task instance.", ioe); 
                            }
                        }
                    });

                    break;
                }
                case QueryTasksOwned: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksOwned(
                            (String) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksOwnedWithParticularStatus: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    
                    // execute
                    List<TaskSummary> results = taskSession.getTasksOwned(
                            (String) cmd.getArguments().get(0),
                            (List<Status>) cmd.getArguments().get(1),
                            (String) cmd.getArguments().get(2));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksAssignedAsBusinessAdministrator: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsBusinessAdministrator(
                            (String) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksAssignedAsPotentialOwner: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsPotentialOwner(
                            (String) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));


                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksAssignedAsPotentialOwnerByStatus: {
                    
                    
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsPotentialOwnerByStatus(
                            (String) cmd.getArguments().get(0),
                            (List<Status>) cmd.getArguments().get(1),
                            (String) cmd.getArguments().get(2));


                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksAssignedAsPotentialOwnerByStatusByGroup: {
                    
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsPotentialOwnerByStatusByGroup(
                            (String) cmd.getArguments().get(0),
                            (List<String>) cmd.getArguments().get(1),
                            (List<Status>) cmd.getArguments().get(2),
                            (String) cmd.getArguments().get(3));


                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }       
                case QueryTasksAssignedAsPotentialOwnerWithGroup: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsPotentialOwner(
                    		(String) cmd.getArguments().get(0),
                            (List<String>) cmd.getArguments().get(1),
                            (String) cmd.getArguments().get(2),
                            (Integer) cmd.getArguments().get(3),
                            (Integer) cmd.getArguments().get(4));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksAssignedAsPotentialOwnerByGroup: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsPotentialOwnerByGroup(
                            (String) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QuerySubTasksAssignedAsPotentialOwner: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    
                    // execute
                    List<TaskSummary> results = taskSession.getSubTasksAssignedAsPotentialOwner(
                            (Long) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1),
                            (String) cmd.getArguments().get(2));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryGetSubTasksByParentTaskId: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getSubTasksByParent(
                            (Long) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }


                case QueryTasksAssignedAsTaskInitiator: {
                     // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsTaskInitiator(
                            (String) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksAssignedAsExcludedOwner: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsExcludedOwner(
                            (String) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksAssignedAsRecipient: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsRecipient(
                            (String) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksAssignedAsTaskStakeholder: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksAssignedAsTaskStakeholder(
                            (String) cmd.getArguments().get(0),
                            (String) cmd.getArguments().get(1));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksByStatusByProcessId: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksByStatusByProcessId(
                            (Long) cmd.getArguments().get(0),
                            (List<Status>) cmd.getArguments().get(1),
                            (String) cmd.getArguments().get(2));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case QueryTasksByStatusByProcessIdByTaskName: {
                    // prepare
                    response = CommandName.QueryTaskSummaryResponse;
                    // execute
                    List<TaskSummary> results = taskSession.getTasksByStatusByProcessIdByTaskName(
                            (Long) cmd.getArguments().get(0),
			                (List<Status>) cmd.getArguments().get(1),
                            (String) cmd.getArguments().get(2),
                            (String) cmd.getArguments().get(3));

                    // return
                    List args = Arrays.asList((new List[] {results}));
                    Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryTaskSummaryResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case RegisterForEventRequest: {
                    // prepare
                    response = CommandName.EventTriggerResponse;
                    EventKey key = (EventKey) cmd.getArguments().get(0);
                    boolean remove = (Boolean) cmd.getArguments().get(1);
                    String uuid = (String) cmd.getArguments().get(2);
                    
                    // execute
                    clients.put(uuid, session);
                    EventTransport transport = new EventTransport(uuid,
									                            cmd.getId(),
									                            clients,
									                            remove);
                    service.getEventKeys().register(key, transport);
                    
                    
                    break;
                }
                case UnRegisterForEventRequest: {
                    
                    EventKey key = (EventKey) cmd.getArguments().get(0);
                    String uuid = (String) cmd.getArguments().get(1);
                    
                    // execute
                    clients.put(uuid, session);
                    
                    service.getEventKeys().removeKey(key);

                    break;
                }    
                case RegisterClient: {
                    // prepare
                    response = CommandName.RegisterClient;
                    String uuid = (String) cmd.getArguments().get(0);

                    // execute
                    clients.put(uuid, session);
                    break;
                }
                /**
                 * TODO: This needs to be be deleted when the .query() functionality finally gets deleted. 
                 */
                case QueryGenericRequest: {
                    // prepare
                	String qlString = (String) cmd.getArguments().get(0);
                	Integer size = (Integer) cmd.getArguments().get(1);
                	Integer offset = (Integer) cmd.getArguments().get(2);
                	
                    // execute
                	List<?> results = taskSession.query(qlString, size, offset);

                    // return
                    List args = Arrays.asList((new List[] {results}));
                	Command resultsCmnd = new Command(cmd.getId(),
                            CommandName.QueryGenericResponse,
                            args);
                    session.write(resultsCmnd);
                    break;
                }
                case NominateTaskRequest:  {
                    // prepare
                	response = CommandName.OperationResponse;
                	long taskId = (Long) cmd.getArguments().get(0);
                	String userId = (String) cmd.getArguments().get(1);
                	List<OrganizationalEntity> potentialOwners = (List<OrganizationalEntity>) cmd.getArguments().get(2);
                	
                    // execute
                	taskSession.nominateTask(taskId, userId, potentialOwners);
                	
                    // return
                	List args = Collections.emptyList();
                	Command resultsCmnd = new Command(cmd.getId(), CommandName.OperationResponse, args);
                	session.write(resultsCmnd);
                	break;
                }
                case SetOutputRequest: {
                    // prepare
                	response = CommandName.OperationResponse;
                	long taskId = (Long) cmd.getArguments().get(0);
                	String userId = (String) cmd.getArguments().get(1);
                	ContentData outputContentData = (ContentData) cmd.getArguments().get(2);
                	
                    // execute
                	taskSession.setOutput(taskId, userId, outputContentData);
                	
                    // return
                	List args = Collections.emptyList();
                	Command resultsCmnd = new Command(cmd.getId(), CommandName.OperationResponse, args);
                	session.write(resultsCmnd);
                	break;
                }
                case DeleteOutputRequest: {
                    // prepare
                	response = CommandName.OperationResponse;
                	long taskId = (Long) cmd.getArguments().get(0);
                	String userId = (String) cmd.getArguments().get(1);
                	
                    // execute
                	taskSession.deleteOutput(taskId, userId);
                	
                    // return
                	List args = Collections.emptyList();
                	Command resultsCmnd = new Command(cmd.getId(), CommandName.OperationResponse, args);
                	session.write(resultsCmnd);
                	
                	break;
                }
                case SetFaultRequest: {
                    // prepare
                        response = CommandName.OperationResponse;
                	long taskId = (Long) cmd.getArguments().get(0);
                	String userId = (String) cmd.getArguments().get(1);
                	FaultData data = (FaultData) cmd.getArguments().get(2);
                	
                    // execute
                	taskSession.setFault(taskId, userId, data);
                	
                    // return
                	List args = Collections.emptyList();
                	Command resultsCmnd = new Command(cmd.getId(), CommandName.OperationResponse, args);
                	session.write(resultsCmnd);
                	break;
                }
                case DeleteFaultRequest: {
                    // prepare
                	response = CommandName.OperationResponse;
                	long taskId = (Long) cmd.getArguments().get(0);
                	String userId = (String) cmd.getArguments().get(1);
                	
                    // execute
                	taskSession.deleteFault(taskId, userId);
                	
                    // return
                	List args = Collections.emptyList();
                	Command resultsCmnd = new Command(cmd.getId(), CommandName.OperationResponse, args);
                	session.write(resultsCmnd);
                	break;
                }
                case SetPriorityRequest: {
                    // prepare
                	response = CommandName.OperationResponse;
                	long taskId = (Long) cmd.getArguments().get(0);
                	String userId = (String) cmd.getArguments().get(1);
                	int priority = (Integer) cmd.getArguments().get(2);
                	
                    // execute
                	taskSession.setPriority(taskId, userId, priority);
                	
                    // return
                	List args = Collections.emptyList();
                	Command resultsCmnd = new Command(cmd.getId(), CommandName.OperationResponse, args);
                	session.write(resultsCmnd);
                	break;
                }
                default: {
                    systemEventListener.debug("Unknown command recieved on server");
                }
            }
        } catch (RuntimeException e) {
            systemEventListener.exception(e.getMessage(),e);
            logger.error(e.getMessage(), e);

            String errorMessage = "Command " + cmd.getName() + " faild due to " + e.getMessage() + ". Please contact task server administrator.";
            List<Object> list = new ArrayList<Object>(1);
            if (e instanceof TaskException) {
                list.add(e);
            } else {
                list.add(new RuntimeException(errorMessage));
            }
            Command resultsCmnd = new Command(cmd.getId(), response, list);
            session.write(resultsCmnd);
        } finally {
            taskSession.dispose();
        }
    }

}

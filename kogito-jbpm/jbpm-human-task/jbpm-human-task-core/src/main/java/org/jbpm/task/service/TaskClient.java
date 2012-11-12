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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.service.TaskClientHandler.AddAttachmentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.AddCommentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.jbpm.task.service.TaskClientHandler.DeleteAttachmentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.DeleteCommentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.jbpm.task.service.TaskClientHandler.QueryGenericResponseHandler;
import org.jbpm.task.service.TaskClientHandler.SetDocumentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.TaskOperationResponseHandler;
import org.jbpm.task.service.TaskClientHandler.TaskSummaryResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.kie.runtime.Environment;

public class TaskClient implements AsyncTaskService{

    private final BaseHandler handler;
	private final AtomicInteger counter;
	private final String name;
	private final TaskClientConnector connector;
        private final Environment environment;
	
        public TaskClient(TaskClientConnector connector) {
            this(connector, null);
        }
        public TaskClient(TaskClientConnector connector, Environment environment) {
		this.connector = connector;
		this.counter = connector.getCounter();
		this.name = connector.getName();
		this.handler = connector.getHandler();
                this.environment = environment;
	}

    public void addTask(Task task, ContentData content, 
                        AddTaskResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object>( 2 );
        args.add( task );
        args.add( content );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.AddTaskRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        
        connector.write( cmd );
    }

    public void getTask(long taskId,
                        GetTaskResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 1 );
        args.add( taskId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.GetTaskRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );

    }

    public void addComment(long taskId,
                           Comment comment,
                           AddCommentResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( taskId );
        args.add( comment );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.AddCommentRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void deleteComment(long taskId,
                              long commentId,
                              DeleteCommentResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( taskId );
        args.add( commentId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.DeleteCommentRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void addAttachment(long taskId,
                              Attachment attachment,
                              Content content,
                              AddAttachmentResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( taskId );
        args.add( attachment );
        args.add( content );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.AddAttachmentRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void deleteAttachment(long taskId,
                                 long attachmentId,
                                 long contentId,
                                 DeleteAttachmentResponseHandler responseHandler ) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( taskId );
        args.add( attachmentId );
        args.add( contentId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.DeleteAttachmentRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        
        connector.write( cmd );
    }

    public void setDocumentContent(long taskId,
                                   Content content,
                                   SetDocumentResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( taskId );
        args.add( content );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.SetDocumentContentRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void getContent(long contentId,
                           GetContentResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 1 );
        args.add( contentId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.GetContentRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void claim(long taskId,
                      String userId,
                      TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( Operation.Claim );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    @Deprecated
    public void claim(long taskId,
		  		      String userId,
		  		      List<String> groupIds,
		              TaskOperationResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 6 );
		args.add( Operation.Claim );
		args.add( taskId );
		args.add( userId );
		args.add( null );
		args.add( null );
		args.add( groupIds );
		Command cmd = new Command( counter.getAndIncrement(),
		                           CommandName.OperationRequest,
		                           args );
		
		handler.addResponseHandler( cmd.getId(),
		                            responseHandler );
		
		connector.write( cmd );
	}

    public void start(long taskId,
                      String userId,
                      TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( Operation.Start );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void stop(long taskId,
                     String userId,
                     TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( Operation.Stop );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void release(long taskId,
                        String userId,
                        TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( Operation.Release );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void suspend(long taskId,
                        String userId,
                        TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( Operation.Suspend );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void resume(long taskId,
                       String userId,
                       TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( Operation.Resume );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void skip(long taskId,
                     String userId,
                     TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( Operation.Skip );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }
    
    public void delegate(long taskId,
                         String userId,
                         String targetUserId,
                         TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 4 );
        args.add( Operation.Delegate );
        args.add( taskId );
        args.add( userId );
        args.add( targetUserId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );      
    }
    
    public void forward(long taskId,
                        String userId,
                        String targetEntityId,
                        TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 4 );
        args.add( Operation.Forward );
        args.add( taskId );
        args.add( userId );
        args.add( targetEntityId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );      
    }    

    public void complete(long taskId,
                         String userId,
                         ContentData outputData,
                         TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 5 );
        args.add( Operation.Complete );
        args.add( taskId );
        args.add( userId );
        args.add( null );
        args.add( outputData );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }
    
    public void completeWithResults(long taskId,
                         String userId,
                         Object results,
                         TaskOperationResponseHandler responseHandler) {
        ContentData outputData = ContentMarshallerHelper.marshal(results, environment);
        List<Object> args = new ArrayList<Object>( 5 );
        args.add( Operation.Complete );
        args.add( taskId );
        args.add( userId );
        args.add( null );
        args.add( outputData );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void fail(long taskId,
                     String userId,
                     FaultData faultData,
                     TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 5 );
        args.add( Operation.Fail );
        args.add( taskId );
        args.add( userId );
        args.add( null );
        args.add( faultData );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        connector.write( cmd );
    }

    public void getTasksOwned(String userId,
                              String language,
                              TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksOwned,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }
    
    public void getTasksOwned(String userId,
                              List<Status> status,
                              String language,
                              TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( userId );
        args.add( status );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksOwnedWithParticularStatus,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void getTaskByWorkItemId(long workItemId,
			                        GetTaskResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>(1);
		args.add(workItemId);
		Command cmd = new Command( counter.getAndIncrement(),
				                   CommandName.QueryTaskByWorkItemId,
				                   args);
		handler.addResponseHandler( cmd.getId(),
				                    responseHandler);
		connector.write(cmd);
	}

    public void getTasksAssignedAsBusinessAdministrator(String userId,
                                                        String language,
                                                        TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsBusinessAdministrator,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void getTasksAssignedAsExcludedOwner(String userId,
                                                String language,
                                                TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsExcludedOwner,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void getTasksAssignedAsPotentialOwner(String userId,
                                                 String language,
                                                 TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsPotentialOwner,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    @Deprecated
    public void getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds,
                                                 String language,
                                                 TaskSummaryResponseHandler responseHandler) {
    	getTasksAssignedAsPotentialOwner(userId, groupIds, language, -1, -1, responseHandler);
    }
    
    @Deprecated
    public void getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds,
                                                 String language, int firstResult, int maxResult,
                                                 TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 5 );
        args.add( userId );
        args.add( groupIds );
        args.add( language );
        args.add( firstResult );
        args.add( maxResult );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsPotentialOwnerWithGroup,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }
    
    public void getSubTasksAssignedAsPotentialOwner(long parentId, String userId,
                                                 String language,
                                                 TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( parentId );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QuerySubTasksAssignedAsPotentialOwner,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }
    public void getSubTasksByParent(long parentId, TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( parentId );
        //@TODO: un hard code this
        args.add( "en-UK" );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryGetSubTasksByParentTaskId,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }
    public void getTasksAssignedAsRecipient(String userId,
                                            String language,
                                            TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsRecipient,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void getTasksAssignedAsTaskInitiator(String userId,
                                                String language,
                                                TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsTaskInitiator,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void getTasksAssignedAsTaskStakeholder(String userId,
                                                  String language,
                                                  TaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsTaskStakeholder,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void registerForEvent(EventKey key,
                                 boolean remove,
                                 EventResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( key );
        args.add( remove );
        args.add( this.name );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.RegisterForEventRequest,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }
    
    public void unregisterForEvent(EventKey key) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( key );
        args.add( this.name );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.UnRegisterForEventRequest,
                                   args );
       
        connector.write( cmd );
    }
    
    /**
     * This method allows the user to exercise a query of his/her choice. 
     * This method will be deleted in future versions. 
     * </p>
     * Only select queries are currently supported, for obvious reasons. 
     * 
     * @param qlString The query string. 
     * @param size     Maximum number of results to return.
     * @param offset   The offset from the beginning of the result list determining the first result. 
     * 
     * @return         The result of the query. 
     */
    @Deprecated
    public void query(String qlString, 
    					 Integer size, 
    					 Integer offset, 
    					 QueryGenericResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object>( 3 );
    	args.add( qlString );
    	args.add( size );
    	args.add( offset );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.QueryGenericRequest,
    							   args );
    	handler.addResponseHandler( cmd.getId(), 
    								responseHandler );
    	connector.write( cmd );
    }
    
    public void register(long taskId,
    		String userId,
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 3 );
    	args.add( Operation.Register );
    	args.add( taskId );
    	args.add( userId );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.OperationRequest,
    							   args );
    	
    	handler.addResponseHandler( cmd.getId(),
    								responseHandler );
    	
    	connector.write( cmd );
    }
    
    public void remove(long taskId,
    		String userId,
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 3 );
    	args.add( Operation.Remove );
    	args.add( taskId );
    	args.add( userId );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.OperationRequest,
    							   args );
    	
    	handler.addResponseHandler( cmd.getId(),
    								responseHandler );
    	
    	connector.write( cmd );
    }
    
    public void nominate(long taskId,
    		String userId,
    		List<OrganizationalEntity> potentialOwners,
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 3 );
    	args.add( taskId );
    	args.add( userId );
    	args.add( potentialOwners );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.NominateTaskRequest,
    							   args );

    	handler.addResponseHandler( cmd.getId(), 
    								responseHandler );

    	connector.write( cmd );
    }

    public void activate(long taskId, 
    		String userId,
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 3 );
    	
    	args.add( Operation.Activate );
    	args.add( taskId );
    	args.add( userId );
    	Command cmd = new Command( counter.getAndIncrement(), 
    							   CommandName.OperationRequest,
    							   args );
    	
    	handler.addResponseHandler( cmd.getId(),
    								responseHandler );
    	
    	connector.write( cmd );
    }
    
    public void exit(long taskId, 
            String userId,
            TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object> ( 3 );
        
        args.add( Operation.Exit );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(), 
                                   CommandName.OperationRequest,
                                   args );
        
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        
        connector.write( cmd );
    }
    
    public void setOutput(long taskId,
    		String userId, 
    		ContentData outputContentData, 
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 3 );
    	args.add( taskId );
    	args.add( userId );
    	args.add( outputContentData );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.SetOutputRequest,
    							   args );
    	
    	handler.addResponseHandler( cmd.getId(),
    								responseHandler );
    	
    	connector.write( cmd );
    }
    
    public void deleteOutput(long taskId,
    		String userId,
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 2 );
    	args.add( taskId );
    	args.add( userId );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.DeleteOutputRequest,
    							   args );
    	
    	handler.addResponseHandler( cmd.getId(),
    								responseHandler );
    	
    	connector.write( cmd );
    }
    
    public void setFault(long taskId, 
    		String userId, 
    		FaultData fault, 
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 3 );
    	args.add( taskId );
    	args.add( userId );
    	args.add( fault );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.SetFaultRequest,
    							   args );
    	
    	handler.addResponseHandler( cmd.getId(),
    								responseHandler );
    	
    	connector.write( cmd );
    }
    
    public void deleteFault(long taskId,
    		String userId,
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 2 );
    	args.add( taskId );
    	args.add( userId );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.DeleteFaultRequest,
    							   args );
    	
    	handler.addResponseHandler( cmd.getId(),
    								responseHandler );
    	
    	connector.write( cmd );
    }
    
    public void setPriority(long taskId,
    		String userId,
    		int priority,
    		TaskOperationResponseHandler responseHandler) {
    	List<Object> args = new ArrayList<Object> ( 3 );
    	args.add( taskId );
    	args.add( userId );
    	args.add( priority );
    	Command cmd = new Command( counter.getAndIncrement(),
    							   CommandName.SetPriorityRequest,
    							   args );
    	
    	handler.addResponseHandler( cmd.getId(),
    								responseHandler );
    	
    	connector.write( cmd );
    }
    
    public boolean connect() {
    	return connector.connect();
    }
    
    public boolean connect(String address, int port) {
    	return connector.connect(address, port);
    }
    
    public void disconnect() throws Exception {
    	connector.disconnect();
    }

    public void claimNextAvailable(String userId, String language, TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.ClaimNextAvailableRequest,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    @Deprecated
    public void claimNextAvailable(String userId, List<String> groupIds, String language, TaskOperationResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( userId );
        args.add( groupIds );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.ClaimNextAvailableRequest,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language, BlockingTaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( status );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsPotentialOwnerByStatus,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }
    
    @Deprecated
    public void getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language, BlockingTaskSummaryResponseHandler responseHandler) {
        List<Object> args = new ArrayList<Object>( 2 );
        args.add( userId );
        args.add( groupIds );
        args.add( status );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsPotentialOwnerByStatusByGroup ,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void getTasksByStatusByProcessId(long processInstanceId, List<Status> status, String language, TaskSummaryResponseHandler responseHandler){
        List<Object> args = new ArrayList<Object>( 3 );
        args.add( processInstanceId );
        args.add( status );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksByStatusByProcessId,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }

    public void getTasksByStatusByProcessIdByTaskName(long processInstanceId, List<Status> status, String taskName, String language, TaskSummaryResponseHandler responseHandler){
        List<Object> args = new ArrayList<Object>( 4 );
        args.add( processInstanceId );
        args.add( status );
        args.add( taskName );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksByStatusByProcessIdByTaskName,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        connector.write( cmd );
    }
}

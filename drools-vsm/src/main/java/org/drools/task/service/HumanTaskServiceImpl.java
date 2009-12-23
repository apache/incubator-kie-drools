package org.drools.task.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.eventmessaging.EventKey;
import org.drools.task.Attachment;
import org.drools.task.Comment;
import org.drools.task.Content;
import org.drools.task.Task;
import org.drools.vsm.GenericConnector;
import org.drools.vsm.HumanTaskService;
import org.drools.vsm.Message;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.AddAttachmentMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.AddCommentMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.AddTaskMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.DeleteAttachmentMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.DeleteCommentMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.GetContentMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.GetTaskMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.SetDocumentMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.TaskOperationMessageResponseHandler;
import org.drools.vsm.task.TaskClientMessageHandlerImpl.TaskSummaryMessageResponseHandler;
import org.drools.vsm.task.eventmessaging.EventMessageResponseHandler;

/**
 * 
 * @author Lucas Amador
 *
 */
public class HumanTaskServiceImpl implements HumanTaskService {

	private final GenericConnector client;
	private final AtomicInteger counter;
	private int sessionId;
	private String clientName;

	public HumanTaskServiceImpl(GenericConnector client, AtomicInteger counter, String clientName, int sessionId) {
		this.client = client;
		this.counter = counter;
		this.clientName = clientName;
		this.sessionId = sessionId;
	}

	public void addTask(Task task, ContentData content, AddTaskMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( task );
		args.add( content );
		Command cmd = new Command( counter.getAndIncrement(), CommandName.AddTaskRequest, args );

		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public Task getTask(long taskId, GetTaskMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 1 );
		args.add( taskId );
		Command cmd = new Command( counter.getAndIncrement(), CommandName.GetTaskRequest, args );

		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );
		client.write(msg, responseHandler);
		return null;
	}

	public void addComment(long taskId, Comment comment, AddCommentMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( taskId );
		args.add( comment );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.AddCommentRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void deleteComment(long taskId, long commentId, DeleteCommentMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( taskId );
		args.add( commentId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.DeleteCommentRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void addAttachment(long taskId, Attachment attachment, Content content, AddAttachmentMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( taskId );
		args.add( attachment );
		args.add( content );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.AddAttachmentRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void deleteAttachment(long taskId, long attachmentId, long contentId, DeleteAttachmentMessageResponseHandler responseHandler ) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( taskId );
		args.add( attachmentId );
		args.add( contentId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.DeleteAttachmentRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void setDocumentContent(long taskId, Content content, SetDocumentMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( taskId );
		args.add( content );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.SetDocumentContentRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void getContent(long contentId, GetContentMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 1 );
		args.add( contentId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.GetContentRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void claim(long taskId, String userId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( Operation.Claim );
		args.add( taskId );
		args.add( userId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void start(long taskId, String userId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( Operation.Start );
		args.add( taskId );
		args.add( userId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void stop(long taskId, String userId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( Operation.Stop );
		args.add( taskId );
		args.add( userId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void release(long taskId, String userId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( Operation.Release );
		args.add( taskId );
		args.add( userId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void suspend(long taskId, String userId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( Operation.Suspend );
		args.add( taskId );
		args.add( userId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void resume(long taskId, String userId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( Operation.Resume );
		args.add( taskId );
		args.add( userId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void skip(long taskId, String userId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( Operation.Skip );
		args.add( taskId );
		args.add( userId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void delegate(long taskId, String userId, String targetUserId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 4 );
		args.add( Operation.Delegate );
		args.add( taskId );
		args.add( userId );
		args.add( targetUserId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);   
	}

	public void forward(long taskId, String userId, String targetEntityId, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 4 );
		args.add( Operation.Forward );
		args.add( taskId );
		args.add( userId );
		args.add( targetEntityId );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);    
	}    

	public void complete(long taskId, String userId, ContentData outputData, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 5 );
		args.add( Operation.Complete );
		args.add( taskId );
		args.add( userId );
		args.add( null );
		args.add( outputData );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void fail(long taskId, String userId, FaultData faultData, TaskOperationMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 5 );
		args.add( Operation.Fail );
		args.add( taskId );
		args.add( userId );
		args.add( null );
		args.add( faultData );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.OperationRequest, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void getTasksOwned(String userId, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( userId );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryTasksOwned, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void getTasksAssignedAsBusinessAdministrator(String userId, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( userId );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryTasksAssignedAsBusinessAdministrator, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void getTasksAssignedAsExcludedOwner(String userId, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( userId );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryTasksAssignedAsExcludedOwner, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void getTasksAssignedAsPotentialOwner(String userId, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( userId );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryTasksAssignedAsPotentialOwner, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}
	public void getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( userId );
		args.add( groupIds );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryTasksAssignedAsPotentialOwnerWithGroup, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( parentId );
		args.add( userId );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QuerySubTasksAssignedAsPotentialOwner, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}
	public void getSubTasksByParent(long parentId, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( parentId );
		//@TODO: un hard code this
		args.add( "en-UK" );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryGetSubTasksByParentTaskId, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}
	public void getTasksAssignedAsRecipient(String userId, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( userId );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryTasksAssignedAsRecipient, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void getTasksAssignedAsTaskInitiator(String userId, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( userId );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryTasksAssignedAsTaskInitiator, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void getTasksAssignedAsTaskStakeholder(String userId, String language, TaskSummaryMessageResponseHandler responseHandler) {
		List<Object> args = new ArrayList<Object>( 2 );
		args.add( userId );
		args.add( language );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.QueryTasksAssignedAsTaskStakeholder, args );
		Message msg = new Message( sessionId, counter.incrementAndGet(), false, cmd );

		client.write(msg, responseHandler);
	}

	public void registerForEvent(EventKey key, boolean remove, EventMessageResponseHandler responseHandler) { //@TODO: look for the event stuff
		List<Object> args = new ArrayList<Object>( 3 );
		args.add( key );
		args.add( remove );
		args.add( clientName );

		Command cmd = new Command( counter.getAndIncrement(), CommandName.RegisterForEventRequest, args );
		int responseId = counter.incrementAndGet();
		Message msg = new Message( sessionId, responseId, false, cmd );

		client.write(msg, responseHandler);
	}

}

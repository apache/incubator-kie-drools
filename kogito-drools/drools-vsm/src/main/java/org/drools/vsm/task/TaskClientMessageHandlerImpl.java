package org.drools.vsm.task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.SystemEventListener;
import org.drools.eventmessaging.EventResponseHandler;
import org.drools.eventmessaging.Payload;
import org.drools.task.Content;
import org.drools.task.Task;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.Command;
import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.GenericMessageHandler;
import org.drools.vsm.Message;
import org.drools.vsm.MessageResponseHandler;
import org.drools.vsm.task.eventmessaging.EventMessageResponseHandler;
import org.drools.vsm.task.responseHandlers.BlockingTaskSummaryMessageResponseHandler;

public class TaskClientMessageHandlerImpl implements GenericMessageHandler {

    /**
     * Listener used for logging
     */
    private SystemEventListener systemEventListener;
    protected Map<Integer, MessageResponseHandler> responseHandlers;

    public TaskClientMessageHandlerImpl(SystemEventListener systemEventListener) {
        this.systemEventListener = systemEventListener;
        this.responseHandlers = new ConcurrentHashMap<Integer, MessageResponseHandler>();;
    }

    
    public void exceptionCaught(GenericIoWriter session,
                                Throwable cause) throws Exception {
        systemEventListener.exception("Uncaught exception on client", cause);
    }

    public void messageReceived(GenericIoWriter session, Message msg) throws Exception {
        Command cmd = (Command) msg.getPayload();
        systemEventListener.debug("Message receieved redirected to the client 1111111111: " + cmd.getName());
        systemEventListener.debug("Arguments : " + Arrays.toString(cmd.getArguments().toArray()));

        switch (cmd.getName()) {
            case OperationResponse: {
                TaskOperationMessageResponseHandler responseHandler = (TaskOperationMessageResponseHandler) responseHandlers.remove(cmd.getId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        responseHandler.setIsDone(true);
                        System.out.println("IS DONDEEEE");
                    }
                }
                break;
            }
            case GetTaskResponse: {
                GetTaskMessageResponseHandler responseHandler = (GetTaskMessageResponseHandler) responseHandlers.remove(cmd.getId());
                if (responseHandler != null) {                    
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        Task task = (Task) cmd.getArguments().get(0);
                        responseHandler.execute(task);
                    }
                }
                break;
            }
            case AddTaskResponse: {
                AddTaskMessageResponseHandler responseHandler = (AddTaskMessageResponseHandler) responseHandlers.remove(msg.getResponseId());
                System.out.println("response id searched: " + msg.getResponseId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        long taskId = (Long) cmd.getArguments().get(0);
                        responseHandler.execute(taskId);
                    }
                }
                break;
            }
            case AddCommentResponse: {
                AddCommentMessageResponseHandler responseHandler = (AddCommentMessageResponseHandler) responseHandlers.remove(cmd.getId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        long commentId = (Long) cmd.getArguments().get(0);
                        responseHandler.execute(commentId);
                    }
                }
                break;
            }
            case DeleteCommentResponse: {
                DeleteCommentMessageResponseHandler responseHandler = (DeleteCommentMessageResponseHandler) responseHandlers.remove(cmd.getId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        responseHandler.setIsDone(true);
                    }
                }
                break;
            }
            case AddAttachmentResponse: {
                AddAttachmentMessageResponseHandler responseHandler = (AddAttachmentMessageResponseHandler) responseHandlers.remove(cmd.getId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        long attachmentId = (Long) cmd.getArguments().get(0);
                        long contentId = (Long) cmd.getArguments().get(1);
                        responseHandler.execute(attachmentId,
                                contentId);
                    }
                }
                break;
            }
            case DeleteAttachmentResponse: {
                DeleteAttachmentMessageResponseHandler responseHandler = (DeleteAttachmentMessageResponseHandler) responseHandlers.remove(cmd.getId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        responseHandler.setIsDone(true);
                    }
                }
                break;
            }
            case GetContentResponse: {
                GetContentMessageResponseHandler responseHandler = (GetContentMessageResponseHandler) responseHandlers.remove(cmd.getId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        Content content = (Content) cmd.getArguments().get(0);
                        responseHandler.execute(content);
                    }
                }
                break;
            }
            case SetDocumentContentResponse: {
                SetDocumentMessageResponseHandler responseHandler = (SetDocumentMessageResponseHandler) responseHandlers.remove(cmd.getId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        long contentId = (Long) cmd.getArguments().get(0);
                        responseHandler.execute(contentId);
                    }
                }
                break;
            }
            case QueryTaskSummaryResponse: {
            	BlockingTaskSummaryMessageResponseHandler responseHandler = (BlockingTaskSummaryMessageResponseHandler) responseHandlers.remove(msg.getResponseId());
                System.out.println("responseHandler: " + responseHandler + " id searched: " + msg.getResponseId());
                if (responseHandler != null) {
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                    } else {
                        List<TaskSummary> results = (List<TaskSummary>) cmd.getArguments().get(0);
                        responseHandler.execute(results);
                    }
                }
                break;
            }
            case EventTriggerResponse: {
                EventMessageResponseHandler responseHandler = (EventMessageResponseHandler) responseHandlers.remove(cmd.getId()); //@TODO view messaging stuff
                System.out.println("EVENT TRIGGER RESPONSE " + responseHandler + " size " + responseHandlers.size() + " id " + cmd.getId());
                if (responseHandler != null) {
                	System.out.println("responseHandler---: " + responseHandler);
                    if (!cmd.getArguments().isEmpty() && cmd.getArguments().get(0) instanceof RuntimeException) {
                        responseHandler.setError((RuntimeException) cmd.getArguments().get(0));
                        System.out.println("EEerror");
                    } else {
                        Payload payload = (Payload) cmd.getArguments().get(0);
                        System.out.println("EExecute ");
                        responseHandler.execute(payload);
                    }
                }
                break;
            }
        }
    }
    
    public static interface GetTaskMessageResponseHandler
            extends
            MessageResponseHandler {
        public void execute(Task task);
    }

    public static interface AddTaskMessageResponseHandler
            extends
            MessageResponseHandler {
        public void execute(long taskId);
    }

    public static interface TaskOperationMessageResponseHandler
            extends
            MessageResponseHandler {
        public void setIsDone(boolean done);
    }

    public static interface AddCommentMessageResponseHandler
            extends
            MessageResponseHandler {
        public void execute(long commentId);
    }

    public static interface DeleteCommentMessageResponseHandler
            extends
            MessageResponseHandler {
        public void setIsDone(boolean done);
    }

    public static interface AddAttachmentMessageResponseHandler
            extends
            MessageResponseHandler {
        public void execute(long attachmentId,
                            long contentId);
    }

    public static interface DeleteAttachmentMessageResponseHandler
            extends
            MessageResponseHandler {
        public void setIsDone(boolean done);
    }

    public static interface SetDocumentMessageResponseHandler
            extends
            MessageResponseHandler {
        public void execute(long contentId);
    }

    public static interface GetContentMessageResponseHandler
            extends
            MessageResponseHandler {
        public void execute(Content content);
    }

    public static interface TaskSummaryMessageResponseHandler
            extends
            MessageResponseHandler {
        public void execute(List<TaskSummary> results);
    }

}
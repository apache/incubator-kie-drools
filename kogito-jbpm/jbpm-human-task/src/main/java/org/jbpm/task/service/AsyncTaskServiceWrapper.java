/*
 * Copyright 2011 JBoss by Red Hat.
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

import org.jbpm.task.TaskService;
import org.jbpm.task.AsyncTaskService;
import java.util.List;
import org.jbpm.eventmessaging.EventKey;
import org.jbpm.process.workitem.wsht.BlockingAddTaskResponseHandler;
import org.jbpm.process.workitem.wsht.BlockingEventResponseHandler;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.responsehandlers.BlockingAddAttachmentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingAddCommentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingDeleteAttachmentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingDeleteCommentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingQueryGenericResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingSetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

/**
 *
 * @author salaboy
 */
public class AsyncTaskServiceWrapper implements TaskService {

    private int timeout = 3000;
    private AsyncTaskService taskService;

    public AsyncTaskServiceWrapper(AsyncTaskService taskService) {
        this.taskService = taskService;
    }

    public void activate(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.activate(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void addAttachment(long taskId, Attachment attachment, Content content) {
        BlockingAddAttachmentResponseHandler responseHandler = new BlockingAddAttachmentResponseHandler();
        taskService.addAttachment(taskId, attachment, content, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        attachment.setId(responseHandler.getAttachmentId());
        content.setId(responseHandler.getContentId());
    }

    public void addComment(long taskId, Comment comment) {
        BlockingAddCommentResponseHandler responseHandler = new BlockingAddCommentResponseHandler();
        taskService.addComment(taskId, comment, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        comment.setId(responseHandler.getCommentId());
    }

    public void addTask(Task task, ContentData content) {
        BlockingAddTaskResponseHandler responseHandler = new BlockingAddTaskResponseHandler();
        taskService.addTask(task, content, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        task.setId(responseHandler.getTaskId());
    }

    public void claim(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.claim(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void claim(long taskId, String userId, List<String> groupIds) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.claim(taskId, userId, groupIds, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void complete(long taskId, String userId, ContentData outputData) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.complete(taskId, userId, outputData, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public boolean connect() {
        return taskService.connect();
    }

    public boolean connect(String address, int port) {
        return taskService.connect(address, port);
    }

    public void delegate(long taskId, String userId, String targetUserId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.delegate(taskId, userId, targetUserId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void deleteAttachment(long taskId, long attachmentId, long contentId) {
        BlockingDeleteAttachmentResponseHandler responseHandler = new BlockingDeleteAttachmentResponseHandler();
        taskService.deleteAttachment(taskId, attachmentId, contentId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void deleteComment(long taskId, long commentId) {
        BlockingDeleteCommentResponseHandler responseHandler = new BlockingDeleteCommentResponseHandler();
        taskService.deleteComment(taskId, commentId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void deleteFault(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.deleteFault(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void deleteOutput(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.deleteOutput(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void disconnect() throws Exception {
        taskService.disconnect();
    }

    public void fail(long taskId, String userId, FaultData faultData) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.fail(taskId, userId, faultData, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void forward(long taskId, String userId, String targetEntityId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.forward(taskId, userId, targetEntityId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public Content getContent(long contentId) {
        BlockingGetContentResponseHandler responseHandler = new BlockingGetContentResponseHandler();
        taskService.getContent(contentId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getContent();
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getSubTasksAssignedAsPotentialOwner(parentId, userId, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getSubTasksByParent(parentId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public Task getTask(long taskId) {
        BlockingGetTaskResponseHandler responseHandler = new BlockingGetTaskResponseHandler();
        taskService.getTask(taskId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getTask();
    }

    public Task getTaskByWorkItemId(long workItemId) {
        BlockingGetTaskResponseHandler responseHandler = new BlockingGetTaskResponseHandler();
        taskService.getTaskByWorkItemId(workItemId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getTask();
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getTasksAssignedAsBusinessAdministrator(userId, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getTasksAssignedAsExcludedOwner(userId, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getTasksAssignedAsPotentialOwner(userId, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getTasksAssignedAsPotentialOwner(userId, groupIds, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getTasksAssignedAsRecipient(userId, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getTasksAssignedAsTaskInitiator(userId, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getTasksAssignedAsTaskStakeholder(userId, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public List<TaskSummary> getTasksOwned(String userId, String language) {
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        taskService.getTasksOwned(userId, language, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.nominate(taskId, userId, potentialOwners, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public List<?> query(String qlString, Integer size, Integer offset) {
        BlockingQueryGenericResponseHandler responseHandler = new BlockingQueryGenericResponseHandler();
        taskService.query(qlString, size, offset, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        return responseHandler.getResults();
    }

    public void register(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.register(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void registerForEvent(EventKey key, boolean remove) {
        BlockingEventResponseHandler responseHandler = new BlockingEventResponseHandler();
        taskService.registerForEvent(key, remove, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void release(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.release(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void remove(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.remove(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void resume(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.resume(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void setDocumentContent(long taskId, Content content) {
        BlockingSetContentResponseHandler responseHandler = new BlockingSetContentResponseHandler();
        taskService.setDocumentContent(taskId, content, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        content.setId(responseHandler.getContentId());
    }

    public void setFault(long taskId, String userId, FaultData fault) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.setFault(taskId, userId, fault, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
        
    }

    public void setOutput(long taskId, String userId, ContentData outputContentData) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.setOutput(taskId, userId, outputContentData, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void setPriority(long taskId, String userId, int priority) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.setPriority(taskId, userId, priority, null);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void skip(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.skip(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void start(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.start(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void stop(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.stop(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }

    public void suspend(long taskId, String userId) {
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        taskService.suspend(taskId, userId, responseHandler);
        try {
            responseHandler.waitTillDone(timeout);
        } catch (Exception e) {
            if (responseHandler.getError() != null) {
                throw responseHandler.getError();
            }
        }
    }
}

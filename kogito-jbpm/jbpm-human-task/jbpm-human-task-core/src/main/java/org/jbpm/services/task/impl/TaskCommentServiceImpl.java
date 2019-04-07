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
package org.jbpm.services.task.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskCommentService;
import org.kie.internal.task.api.TaskPersistenceContext;

/**
 *
 */
public class TaskCommentServiceImpl implements TaskCommentService {
     
    private TaskPersistenceContext persistenceContext;

	public TaskCommentServiceImpl() {
    }
	
	public TaskCommentServiceImpl(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}
    
    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

    public long addComment(long taskId, Comment comment) {
        Task task = persistenceContext.findTask(taskId);
        
        if (persistenceContext.findUser(comment.getAddedBy().getId()) == null) {
            persistenceContext.persistUser(comment.getAddedBy());
        }
        persistenceContext.persistComment(comment);
        persistenceContext.addCommentToTask(comment, task);
        return comment.getId();
       
    }

    public void deleteComment(long taskId, long commentId) {
        Task task = persistenceContext.findTask(taskId);
        Comment comment = persistenceContext.findComment(commentId);
        persistenceContext.removeCommentFromTask(comment, task);
        persistenceContext.removeComment(comment);
    }

    public List<Comment> getAllCommentsByTaskId(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        if (task != null) {
        	return new ArrayList<Comment>(task.getTaskData().getComments());
        }
        
        return new ArrayList<Comment>();
    }

    public Comment getCommentById(long commentId) {
        return persistenceContext.findComment(commentId);
    }
    
}

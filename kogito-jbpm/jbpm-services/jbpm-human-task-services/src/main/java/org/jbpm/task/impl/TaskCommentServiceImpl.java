/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.impl;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.task.Comment;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskCommentService;

/**
 *
 */
@Transactional
@ApplicationScoped
public class TaskCommentServiceImpl implements TaskCommentService{
    @Inject 
    private JbpmServicesPersistenceManager pm;

    public TaskCommentServiceImpl() {
    }

    public long addComment(long taskId, Comment comment) {
        Task task = pm.find(Task.class, taskId);
        pm.persist(comment);
        task.getTaskData().addComment(comment);
        return comment.getId();
       
    }

    public void deleteComment(long taskId, long commentId) {
        Task task = pm.find(Task.class, taskId);
        Comment comment = pm.find(Comment.class, commentId);
        task.getTaskData().removeComment(commentId);
        pm.remove(comment);
    }

    public List<Comment> getAllCommentsByTaskId(long taskId) {
        Task task = pm.find(Task.class, taskId);
 	return task.getTaskData().getComments();
    }

    public Comment getCommentById(long commentId) {
        return pm.find(Comment.class, commentId);
    }
    
}

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
package org.jbpm.task.identity;

import java.util.List;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.kie.internal.task.api.TaskCommentService;
import org.kie.internal.task.api.model.Comment;

/**
 *
 */
@Decorator
public class UserGroupTaskCommentDecorator extends AbstractUserGroupCallbackDecorator implements TaskCommentService {

    @Inject
    @Delegate
    private TaskCommentService commentService;


    public long addComment(long taskId, Comment comment) {
        doCallbackOperationForComment(comment);
        long commentId = commentService.addComment(taskId, comment);
        return commentId;
    }

    public void deleteComment(long taskId, long commentId) {
        commentService.deleteComment(taskId, commentId);
    }

    public List<Comment> getAllCommentsByTaskId(long taskId) {
        return commentService.getAllCommentsByTaskId(taskId);
    }

    public Comment getCommentById(long commentId) {
        return commentService.getCommentById(commentId);
    }

    private void doCallbackOperationForComment(Comment comment) {
        if (comment != null) {
            if (comment.getAddedBy() != null) {
                doCallbackUserOperation(comment.getAddedBy().getId());
            }
        }
    }

}

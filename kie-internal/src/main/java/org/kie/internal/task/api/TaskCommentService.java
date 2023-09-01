package org.kie.internal.task.api;

import java.util.List;

import org.kie.api.task.model.Comment;

/**
 * The Task Comment Service will handle all the
 *  operations related with the Comments associated with
 *  a Task
 */
public interface TaskCommentService {

    long addComment(long taskId, Comment comment);

    void deleteComment(long taskId, long commentId);

    List<Comment> getAllCommentsByTaskId(long taskId);

    Comment getCommentById(long commentId);
}

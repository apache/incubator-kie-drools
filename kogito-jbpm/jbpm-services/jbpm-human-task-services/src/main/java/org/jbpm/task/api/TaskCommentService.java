/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.List;
import org.jbpm.task.Comment;

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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.List;
import org.jbpm.task.Content;

/**
 * The Task Content Service is intended to handle
 *  the information which is stored inside a Task.
 *  The information inside the Task Content represents
 *   the information required for the Task to be completed.
 *  This information can be divided into:
 *      - Task Input: The data used by the Human Actor
 *                    as a context to do the job
 *      - Task Output: The data entered by the Human Actor
 *                   as the result of the work that is being done.
 */

public interface TaskContentService {

    long addContent(long taskId, Content content);

    void deleteContent(long taskId, long contentId);

    List<Content> getAllContentByTaskId(long taskId);

    Content getContentById(long contentId);
}

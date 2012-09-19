/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.List;
import org.jbpm.task.Attachment;
import org.jbpm.task.Content;

/**
 * The Task Attachment Service will deal with all the 
 *  functionality related with Task Attachments. Different
 *  implementations can be provided to handle the Task Attachments.
 */

public interface TaskAttachmentService {

    long addAttachment(long taskId, Attachment attachment, Content content);

    void deleteAttachment(long taskId, long attachmentId);

    List<Attachment> getAllAttachmentsByTaskId(long taskId);

    Attachment getAttachmentById(long attachId);
}

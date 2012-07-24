/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.List;
import org.jbpm.task.Attachment;

/**
 *
 * @author salaboy
 */

public interface TaskAttachmentService {

    long addAttachment(long taskId, Attachment attachment);

    void deleteAttachment(long taskId, long attachmentId);

    List<Attachment> getAttachments(long taskId);

    Attachment getAttachmentById(long attachId);
}

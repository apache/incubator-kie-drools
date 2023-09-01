package org.kie.internal.task.api;

import java.util.List;

import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;

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

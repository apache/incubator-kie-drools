package org.kie.internal.task.api.model;

import java.util.Date;

import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.User;

public interface InternalAttachment extends Attachment {

    void setId(long id);

    void setName(String name);

    AccessType getAccessType();

    void setAccessType(AccessType accessType);

    void setContentType(String contentType);

    void setAttachedAt(Date attachedAt);

    void setAttachedBy(User attachedBy);

    void setContent(Content content);

    void setSize(int size);

    void setAttachmentContentId(long contentId);

}

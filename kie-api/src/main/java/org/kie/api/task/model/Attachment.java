package org.kie.api.task.model;

import java.io.Externalizable;
import java.util.Date;

public interface Attachment extends Externalizable {

    Long getId();

    String getName();

    String getContentType();

    Date getAttachedAt();

    User getAttachedBy();

    int getSize();

    long getAttachmentContentId();

}

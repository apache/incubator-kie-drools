package org.kie.internal.task.api.model;

import java.io.Externalizable;

public interface ContentData extends Externalizable {

    AccessType getAccessType();

    void setAccessType(AccessType accessType);

    String getType();

    void setType(String type);

    byte[] getContent();

    void setContent(byte[] content);
    
    Object getContentObject();
    
    void setContentObject(Object object);
}

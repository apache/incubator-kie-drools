package org.kie.internal.task.api.model;

import org.kie.api.task.model.Content;

public interface InternalContent extends Content {

    void setId(long id);

    void setContent(byte[] content);

}

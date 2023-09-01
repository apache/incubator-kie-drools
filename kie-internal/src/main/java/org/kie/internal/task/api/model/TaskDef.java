package org.kie.internal.task.api.model;

import java.io.Externalizable;


public interface TaskDef extends Externalizable {

    long getId();

    void setId(long id);

    String getName();

    void setName(String name);

    int getPriority();

    void setPriority(int priority);

}

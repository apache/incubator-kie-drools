package org.kie.internal.task.api.model;

import java.io.Externalizable;


public interface BooleanExpression extends Externalizable {

    Long getId();

    void setId(long id);

    String getType();

    void setType(String type);

    String getExpression();

    void setExpression(String expression);

}

package org.kie.api.task.model;

import java.io.Externalizable;
import java.util.Date;


public interface Comment extends Externalizable  {

    Long getId();

    String getText();

    Date getAddedAt();

    User getAddedBy();

}

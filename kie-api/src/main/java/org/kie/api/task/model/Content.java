package org.kie.api.task.model;

import java.io.Externalizable;

public interface Content extends Externalizable {

    Long getId();

    byte[] getContent();

}

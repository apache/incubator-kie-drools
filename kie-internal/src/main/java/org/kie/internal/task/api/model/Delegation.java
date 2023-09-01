package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.List;

import org.kie.api.task.model.OrganizationalEntity;


public interface Delegation  extends Externalizable {

    AllowedToDelegate getAllowed();

    void setAllowed(AllowedToDelegate allowedToDelegate);

    List<OrganizationalEntity> getDelegates();

    void setDelegates(List<OrganizationalEntity> delegates);

}

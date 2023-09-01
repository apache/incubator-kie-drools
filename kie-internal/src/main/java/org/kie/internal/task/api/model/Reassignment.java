package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.List;

import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;

public interface Reassignment extends Externalizable {

    Long getId();

    void setId(long id);

    List<I18NText> getDocumentation();

    void setDocumentation(List<I18NText> documentation);

    List<OrganizationalEntity> getPotentialOwners();

    void setPotentialOwners(List<OrganizationalEntity> potentialOwners);

}

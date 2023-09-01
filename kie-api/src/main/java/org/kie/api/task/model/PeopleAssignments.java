package org.kie.api.task.model;

import java.io.Externalizable;
import java.util.List;


public interface PeopleAssignments extends Externalizable {

    User getTaskInitiator();

    List<OrganizationalEntity> getPotentialOwners();

    List<OrganizationalEntity> getBusinessAdministrators();

}

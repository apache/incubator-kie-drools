package org.kie.internal.task.api.model;

import java.util.List;

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.User;


public interface InternalPeopleAssignments extends PeopleAssignments {

    void setTaskInitiator(User taskInitiator);

    void setPotentialOwners(List<OrganizationalEntity> potentialOwners);

    List<OrganizationalEntity> getExcludedOwners();

    void setExcludedOwners(List<OrganizationalEntity> excludedOwners);

    List<OrganizationalEntity> getTaskStakeholders();

    void setTaskStakeholders(List<OrganizationalEntity> taskStakeholders);

    void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators);

    List<OrganizationalEntity> getRecipients();

    void setRecipients(List<OrganizationalEntity> recipients);

}

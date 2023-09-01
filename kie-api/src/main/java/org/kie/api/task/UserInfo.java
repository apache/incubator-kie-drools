package org.kie.api.task;

import java.util.Iterator;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;

public interface UserInfo {

    String getDisplayName(OrganizationalEntity entity);

    Iterator<OrganizationalEntity> getMembersForGroup(Group group);

    boolean hasEmail(Group group);

    String getEmailForEntity(OrganizationalEntity entity);

    String getLanguageForEntity(OrganizationalEntity entity);

}

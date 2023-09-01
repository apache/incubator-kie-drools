package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.List;

import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;

public interface Notification extends Externalizable  {

    Long getId();

    void setId(long id);

    NotificationType getNotificationType();

    List<I18NText> getDocumentation();

    void setDocumentation(List<I18NText> documentation);

    int getPriority();

    void setPriority(int priority);

    List<OrganizationalEntity> getRecipients();

    void setRecipients(List<OrganizationalEntity> recipients);

    List<OrganizationalEntity> getBusinessAdministrators();

    void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators);

    List<I18NText> getNames();

    void setNames(List<I18NText> names);

    List<I18NText> getSubjects();

    void setSubjects(List<I18NText> subjects);

    List<I18NText> getDescriptions();

    void setDescriptions(List<I18NText> descriptions);

}

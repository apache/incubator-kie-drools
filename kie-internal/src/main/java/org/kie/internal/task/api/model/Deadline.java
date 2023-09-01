package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.Date;
import java.util.List;

import org.kie.api.task.model.I18NText;


public interface Deadline extends Externalizable {

    Boolean isEscalated();

    void setEscalated(Boolean escalated);

    long getId();

    void setId(long id);

    List<I18NText> getDocumentation();

    void setDocumentation(List<I18NText> documentation);

    Date getDate();

    void setDate(Date date);

    List<Escalation> getEscalations();

    void setEscalations(List<Escalation> escalations);

}

package org.kie.api.task.model;

import java.io.Externalizable;
import java.util.List;

public interface Task extends Externalizable {

    Long getId();

    Integer getPriority();

    List<I18NText> getNames();

    List<I18NText> getSubjects();

    List<I18NText> getDescriptions();

    String getName();

    String getSubject();

    String getDescription();

    PeopleAssignments getPeopleAssignments();

    TaskData getTaskData();

    String getTaskType();

    Boolean isArchived();

    Integer getVersion();

    String getFormName();

}

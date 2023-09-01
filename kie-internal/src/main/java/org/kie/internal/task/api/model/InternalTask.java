package org.kie.internal.task.api.model;

import java.util.List;

import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;

public interface InternalTask extends Task {

    void setId(Long id);

    void setArchived(Boolean archived);

    void setPriority(Integer priority);

    void setNames(List<I18NText> names);

    void setFormName(String formName);

    void setSubjects(List<I18NText> subjects);

    void setDescriptions(List<I18NText> descriptions);

    void setPeopleAssignments(PeopleAssignments peopleAssignments);

    Delegation getDelegation();

    void setDelegation(Delegation delegation);

    void setTaskData(TaskData taskData);

    Deadlines getDeadlines();

    void setDeadlines(Deadlines deadlines);

    void setTaskType(String taskType);

    SubTasksStrategy getSubTaskStrategy();

    void setSubTaskStrategy(SubTasksStrategy subTaskStrategy);

    void setName(String name);

    void setSubject(String subject);
    
    void setDescription(String description);

}

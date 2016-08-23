/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.task.api.model;

import java.util.List;

import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;

public interface InternalTask extends Task {

    void setId(long id);

    Boolean isArchived();

    void setArchived(Boolean archived);

    int getVersion();

    void setPriority(int priority);

    void setNames(List<I18NText> names);

    void setFormName(String formName);

    String getFormName();

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

/*
 * Copyright 2010 JBoss Inc
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

import java.io.Externalizable;
import java.util.List;

public interface Task extends Externalizable {
   
    Long getId();

    void setId(long id);

    Boolean isArchived();

    void setArchived(Boolean archived);
    
    int getVersion();

    int getPriority();

    void setPriority(int priority);

    List<I18NText> getNames();

    void setNames(List<I18NText> names);

    List<I18NText> getSubjects();

    void setSubjects(List<I18NText> subjects);

    List<I18NText> getDescriptions();

    void setDescriptions(List<I18NText> descriptions);

    PeopleAssignments getPeopleAssignments();

    void setPeopleAssignments(PeopleAssignments peopleAssignments);

    Delegation getDelegation();

    void setDelegation(Delegation delegation);

    TaskData getTaskData();

    void setTaskData(TaskData taskData);

    Deadlines getDeadlines();

    void setDeadlines(Deadlines deadlines);

    String getTaskType();

    void setTaskType(String taskType);

    Short getArchived();
   
    SubTasksStrategy getSubTaskStrategy();

    void setSubTaskStrategy(SubTasksStrategy subTaskStrategy);

}

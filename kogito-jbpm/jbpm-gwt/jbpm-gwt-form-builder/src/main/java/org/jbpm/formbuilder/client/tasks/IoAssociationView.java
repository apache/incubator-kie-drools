/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.client.tasks;

import java.util.List;

import org.jbpm.formbuilder.shared.task.TaskRef;

public interface IoAssociationView {

    interface Presenter {

        TaskRow newTaskRow(TaskRef task, boolean even);

        void addQuickFormHandling(TaskRow row);
        
    };
    
    SearchFilterView getSearch();

    void setTasks(List<TaskRef> tasks);

    void setSelectedTask(TaskRef selectedTask);

    void disableSearch();

    TaskRow createTaskRow(TaskRef task, boolean even);
}

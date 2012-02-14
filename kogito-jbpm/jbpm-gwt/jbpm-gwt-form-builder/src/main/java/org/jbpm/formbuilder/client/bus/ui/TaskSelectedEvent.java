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
package org.jbpm.formbuilder.client.bus.ui;

import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies that a task has been univocally identified on the UI
 */
public class TaskSelectedEvent extends GwtEvent<TaskSelectedHandler> {

    public static final Type<TaskSelectedHandler> TYPE = new Type<TaskSelectedHandler>();
    
    private final TaskRef selectedTask;
    
    public TaskSelectedEvent(TaskRef selectedTask) {
        this.selectedTask = selectedTask;
    }
    
    public TaskRef getSelectedTask() {
        return selectedTask;
    }

    @Override
    public Type<TaskSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TaskSelectedHandler handler) {
        handler.onSelectedTask(this);
    }

    
}

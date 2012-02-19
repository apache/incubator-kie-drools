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
package org.jbpm.formbuilder.client.bus;

import java.util.List;

import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies that the server has found new tasks that match a given filter
 */
public class ExistingTasksResponseEvent extends GwtEvent<ExistingTasksResponseHandler> {

    public static final Type<ExistingTasksResponseHandler> TYPE = new Type<ExistingTasksResponseHandler>();
    
    private final List<TaskRef> tasks;
    private final String filter;
    
    public ExistingTasksResponseEvent(List<TaskRef> tasks, String filter) {
        super();
        this.tasks = tasks;
        this.filter = filter;
    }
    
    public List<TaskRef> getTasks() {
        return tasks;
    }
    
    public String getFilter() {
        return filter;
    }

    @Override
    public Type<ExistingTasksResponseHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ExistingTasksResponseHandler handler) {
        handler.onEvent(this);
    }

}

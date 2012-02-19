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

import com.google.gwt.event.shared.GwtEvent; 

/**
 * Notifies that the properties of the form have been populated
 */
public class FormDataPopulatedEvent extends GwtEvent<FormDataPopulatedHandler> {

    public static final Type<FormDataPopulatedHandler> TYPE = new Type<FormDataPopulatedHandler>();

    private final String action;
    private final String method;
    private final String taskId;
    private final String processId;
    private final String enctype;
    private final String name;
    
    public FormDataPopulatedEvent(String action, String method, String taskId, String processId, String enctype, String name) {
        this.name = name;
        this.action = action;
        this.method = method;
        this.taskId = taskId;
        this.processId = processId;
        this.enctype = enctype;
    }
    
    @Override
    public Type<FormDataPopulatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FormDataPopulatedHandler handler) {
        handler.onEvent(this);
    }

    public String getAction() {
        return action;
    }

    public String getMethod() {
        return method;
    }

    public String getTaskId() {
        return taskId;
    }
    
    public String getProcessId() {
        return processId;
    }

    public String getEnctype() {
        return enctype;
    }

    public String getName() {
        return name;
    }
}

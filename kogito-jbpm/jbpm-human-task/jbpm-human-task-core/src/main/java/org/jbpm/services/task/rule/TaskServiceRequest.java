/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.services.task.exception.CannotAddTaskException;
import org.kie.api.task.model.User;
import org.kie.internal.task.exception.TaskException;

public class TaskServiceRequest {
    
    private String type;
    private User user;
    private Map<String, Object> parameters;
    private boolean allowed = true;
    private List<String> reasons;
    private Class<? extends TaskException> exceptionClass;
    
    public TaskServiceRequest(String type, User user, Map<String, Object> parameters) {
        this.type = type;
        this.user = user;
        this.parameters = parameters;
    }
    
    public String getType() {
        return type;
    }
    
    public User getUser() {
        return user;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public Object getParameter(String name) {
        if (parameters == null) {
            return null;
        }
        return parameters.get(name);
    }
    
    public boolean isAllowed() {
        return allowed;
    }
    
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
    
    public List<String> getReasons() {
        return reasons;
    }
    
    public void addReason(String reason) {
        if (reasons == null) {
            reasons = new ArrayList<String>();
        }
        reasons.add(reason);
    }
    
    public TaskException getException(String message) {
        if (this.exceptionClass != null) {            
            try {
                return this.exceptionClass.getConstructor(new Class[]{String.class}).newInstance(new Object[]{message});
            } catch (Exception e) {
                //do nothing
            }
        }
        
        return new CannotAddTaskException(message);
    }

    public Class<? extends TaskException> getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(Class<? extends TaskException> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

}
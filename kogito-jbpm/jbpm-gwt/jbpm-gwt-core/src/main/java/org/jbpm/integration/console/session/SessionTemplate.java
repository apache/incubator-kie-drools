/**
 * Copyright 2012 JBoss Inc
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
package org.jbpm.integration.console.session;

import java.util.List;
import java.util.Map;

public class SessionTemplate {

    private String businessKey;
    private boolean imported;
    
    private String persistenceUnit;
    
    private Map<String, String> properties;
    
    private Map<String, String> workItemHandlers;
    
    private List<String> eventListeners;
    
    private Map<String, String> environmentEntries;

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String id) {
        this.businessKey = id;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public String getPersistenceUnit() {
        return persistenceUnit;
    }

    public void setPersistenceUnit(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getWorkItemHandlers() {
        return workItemHandlers;
    }

    public void setWorkItemHandlers(Map<String, String> workItemHandlers) {
        this.workItemHandlers = workItemHandlers;
    }

    public List<String> getEventListeners() {
        return eventListeners;
    }

    public void setEventListeners(List<String> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public Map<String, String> getEnvironmentEntries() {
        return environmentEntries;
    }

    public void setEnvironmentEntries(Map<String, String> environmentEntries) {
        this.environmentEntries = environmentEntries;
    }
    
    
}

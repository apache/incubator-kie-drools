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

package org.drools.core.process.instance.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.core.process.instance.WorkItem;

public class WorkItemImpl extends TypedWorkItemImpl<Map<String, Object>, Map<String, Object>> implements WorkItem {

    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, Object> results = new HashMap<String, Object>();

    @Override
    public org.kie.api.runtime.process.WorkItem asWorkItem() {
        return this;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void setParameter(String name, Object value) {
        this.parameters.put(name, value);
    }

    public Object getParameter(String name) {
        return parameters.get(name);
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setResults(Map<String, Object> results) {
        if (results != null) {
            this.results = results;
        }
    }

    public void setResult(String name, Object value) {
        results.put(name, value);
    }

    public Object getResult(String name) {
        return results.get(name);
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public String toString() {
        StringBuilder b = new StringBuilder("WorkItem ");
        b.append(getId());
        b.append(" [name=");
        b.append(getName());
        b.append(", state=");
        b.append(getState());
        b.append(", processInstanceId=");
        b.append(getProcessInstanceId());
        b.append(", parameters{");
        for (Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            b.append(entry.getKey());
            b.append("=");
            b.append(entry.getValue());
            if (iterator.hasNext()) {
                b.append(", ");
            }
        }
        b.append("}]");
        return b.toString();
    }
}

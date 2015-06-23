/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.kie.api.runtime.process.WorkItem;

import java.util.Map;

public class WorkItemKieAdapter implements WorkItem {

    public org.drools.runtime.process.WorkItem delegate;

    public WorkItemKieAdapter(org.drools.runtime.process.WorkItem delegate) {
        this.delegate = delegate;
    }

    public long getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public int getState() {
        return delegate.getState();
    }

    public Object getParameter(String name) {
        return delegate.getParameter(name);
    }

    public Map<String, Object> getParameters() {
        return delegate.getParameters();
    }

    public Object getResult(String name) {
        return delegate.getResult(name);
    }

    public Map<String, Object> getResults() {
        return delegate.getResults();
    }

    public long getProcessInstanceId() {
        return delegate.getProcessInstanceId();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkItemKieAdapter && delegate.equals(((WorkItemKieAdapter)obj).delegate);
    }
}

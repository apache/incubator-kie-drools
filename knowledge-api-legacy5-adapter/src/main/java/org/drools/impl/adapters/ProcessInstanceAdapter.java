/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.api.runtime.process.ProcessInstance;

public class ProcessInstanceAdapter implements org.drools.runtime.process.ProcessInstance {

    public final ProcessInstance delegate;

    public ProcessInstanceAdapter(ProcessInstance delegate) {
        this.delegate = delegate;
    }

	public ProcessInstance getDelegate() {
		return delegate;
	}
	
    public String getProcessId() {
        return delegate.getProcessId();
    }

    public org.drools.definition.process.Process getProcess() {
        return new ProcessAdapter(delegate.getProcess());
    }

    public long getId() {
        return delegate.getId();
    }

    public String getProcessName() {
        return delegate.getProcessName();
    }

    public int getState() {
        return delegate.getState();
    }

    public void signalEvent(String type, Object event) {
        delegate.signalEvent(type, event);
    }

    public String[] getEventTypes() {
        return delegate.getEventTypes();
    }

    public static List<org.drools.runtime.process.ProcessInstance> adaptProcessInstances(Collection<ProcessInstance> processes) {
        List<org.drools.runtime.process.ProcessInstance> result = new ArrayList<org.drools.runtime.process.ProcessInstance>();
        for (ProcessInstance process : processes) {
            result.add(new ProcessInstanceAdapter(process));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProcessInstanceAdapter && delegate.equals(((ProcessInstanceAdapter)obj).delegate);
    }
}

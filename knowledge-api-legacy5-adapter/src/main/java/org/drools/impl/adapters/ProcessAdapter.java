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

import static org.drools.impl.adapters.AdapterUtil.adaptKnowledgeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.kie.api.definition.process.Process;

public class ProcessAdapter implements org.drools.definition.process.Process {

    public final Process delegate;

    public ProcessAdapter(Process delegate) {
        this.delegate = delegate;
    }

	public Process getDelegate() {
		return delegate;
	}
	
    public KnowledgeType getKnowledgeType() {
        return adaptKnowledgeType(delegate.getKnowledgeType());
    }

    public String getNamespace() {
        return delegate.getNamespace();
    }

    public String getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public String getVersion() {
        return delegate.getVersion();
    }

    public String getPackageName() {
        return delegate.getPackageName();
    }

    public String getType() {
        return delegate.getType();
    }

    public Map<String, Object> getMetaData() {
        return delegate.getMetaData();
    }

    public Object getMetaData(String name) {
        return delegate.getMetaData().get(name);
    }

    public static Collection<org.drools.definition.process.Process> adaptProcesses(Collection<Process> processes) {
        Collection<org.drools.definition.process.Process> result = new ArrayList<org.drools.definition.process.Process>();
        for (org.kie.api.definition.process.Process process : processes) {
            result.add(new ProcessAdapter(process));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProcessAdapter && delegate.equals(((ProcessAdapter)obj).delegate);
    }
}

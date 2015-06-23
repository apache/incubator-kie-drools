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

import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import java.util.Map;

public class WorkItemManagerKieAdapter implements WorkItemManager {

    public org.drools.runtime.process.WorkItemManager delegate;

    public WorkItemManagerKieAdapter(org.drools.runtime.process.WorkItemManager delegate) {
        this.delegate = delegate;
    }

    public void completeWorkItem(long id, Map<String, Object> results) {
        delegate.completeWorkItem(id, results);
    }

    public void abortWorkItem(long id) {
        delegate.abortWorkItem(id);
    }

    public void registerWorkItemHandler(String workItemName,
                                        WorkItemHandler handler) {
        delegate.registerWorkItemHandler(workItemName, new WorkItemHandlerKieAdapter(handler));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkItemManagerKieAdapter && delegate.equals(((WorkItemManagerKieAdapter)obj).delegate);
    }
}

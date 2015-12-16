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

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkItemHandler;

public class WorkItemHandlerKieAdapter implements org.drools.runtime.process.WorkItemHandler {

    private WorkItemHandler delegate;

    public WorkItemHandlerKieAdapter(WorkItemHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        delegate.executeWorkItem(new WorkItemKieAdapter(workItem), new WorkItemManagerKieAdapter(manager));
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        delegate.abortWorkItem(new WorkItemKieAdapter(workItem), new WorkItemManagerKieAdapter(manager));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkItemHandlerKieAdapter && delegate.equals(((WorkItemHandlerKieAdapter)obj).delegate);
    }
}

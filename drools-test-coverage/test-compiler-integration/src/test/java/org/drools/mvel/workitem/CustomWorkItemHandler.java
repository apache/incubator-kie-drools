/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.mvel.workitem;

import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class CustomWorkItemHandler implements WorkItemHandler {
    
    @SuppressWarnings("unused")
    private StatefulKnowledgeSession session;
    
    public CustomWorkItemHandler(StatefulKnowledgeSession session) {
        this.session = session;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // dummy work item handler implementation

    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // dummy work item handler implementation

    }

}

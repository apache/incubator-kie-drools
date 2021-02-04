/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.bpmn2.handler;

import org.kie.api.runtime.process.ProcessWorkItemHandlerException;
import org.kie.api.runtime.process.ProcessWorkItemHandlerException.HandlingStrategy;
import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;


public class ErrornousWorkItemHandler implements KogitoWorkItemHandler {
    
    private String processId;
    private HandlingStrategy strategy;
    
    private WorkItem workItem;

    public ErrornousWorkItemHandler(String processId, HandlingStrategy strategy) {
        super();
        this.processId = processId;
        this.strategy = strategy;
    }

    @Override
    public void executeWorkItem( KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        this.workItem = workItem;
        if (processId != null && strategy != null) {
            
            if (workItem.getParameter("isCheckedCheckbox") != null) {
                manager.completeWorkItem(workItem.getStringId(), workItem.getParameters());
            } else {
            
                throw new ProcessWorkItemHandlerException(processId, strategy, new RuntimeException("On purpose"));
            }
        }
        
        manager.completeWorkItem(workItem.getStringId(), null);
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        this.workItem = workItem;

    }

    public WorkItem getWorkItem() {
        return workItem;
    }
}

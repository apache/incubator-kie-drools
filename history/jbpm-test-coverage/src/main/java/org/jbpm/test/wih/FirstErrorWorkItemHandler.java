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

package org.jbpm.test.wih;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class FirstErrorWorkItemHandler implements WorkItemHandler {

    private List<Long> processedWorkItems = new ArrayList<Long>();

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        long processInstanceId = workItem.getProcessInstanceId();
        if (!processedWorkItems.contains(processInstanceId)) {
            processedWorkItems.add(processInstanceId);
            throw new RuntimeException("Error");
        }
        manager.completeWorkItem(workItem.getId(), new HashMap<String, Object>());
        processedWorkItems.remove(processInstanceId);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());
    }

}

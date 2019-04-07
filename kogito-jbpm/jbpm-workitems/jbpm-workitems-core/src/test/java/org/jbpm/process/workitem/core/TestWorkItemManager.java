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

package org.jbpm.process.workitem.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class TestWorkItemManager implements WorkItemManager {

    private Map<Long, Map<String, Object>> completedWorkItems = new HashMap<>();
    private List<Long> abortedWorkItems = new ArrayList<>();

    @Override
    public void completeWorkItem(long id,
                                 Map<String, Object> results) {
        // some workitem handlers do not set results (default it to null)
        if(results == null) {
            results = new HashMap<>();
        }

        completedWorkItems.put(id,
                               results);
    }

    @Override
    public void abortWorkItem(long id) {
        abortedWorkItems.add(id);
    }

    @Override
    public void registerWorkItemHandler(String workItemName,
                                        WorkItemHandler handler) {

    }

    public Map<String, Object> getResults(Long id) {
        return completedWorkItems.get(id);
    }

    public Map<Long, Map<String, Object>> getResults() {
        return completedWorkItems;
    }

    public List<Long> getAbortedWorkItems() {
        return abortedWorkItems;
    }
}

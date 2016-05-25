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

package org.drools.core.process.instance;

import java.util.Map;
import java.util.Set;

public interface WorkItemManager extends org.kie.api.runtime.process.WorkItemManager {

    void internalExecuteWorkItem(WorkItem workItem);

    void internalAddWorkItem(WorkItem workItem);

    void internalAbortWorkItem(long id);
    
    Set<WorkItem> getWorkItems();

    WorkItem getWorkItem(long id);

    void clear();
    
    public void signalEvent(String type, Object event);
    
    public void signalEvent(String type, Object event, long processInstanceId);

    void dispose();
    
    void retryWorkItem( Long workItemID, Map<String, Object> params ) ;

}

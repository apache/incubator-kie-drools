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

import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.command.runtime.process.GetWorkItemIdsCommand;

public interface WorkItemManager extends org.kie.api.runtime.process.WorkItemManager {

    void internalExecuteWorkItem(WorkItem workItem);

    void internalAddWorkItem(WorkItem workItem);

    /**
     * This method is called by the jBPM engine when cancelling a WorkItemNodeInstance
     * @param id
     */
    void internalAbortWorkItem(long id);

    /**
     * This method does (really) not seem to be used anywhere -- and is also *not* implemented by the
     * JPAWorkItemManager. The one exception is the {@link GetWorkItemIdsCommand} command -- however, this is also
     * not used anywhere.
     * </p>
     * @return A set of the current {@link WorkItem}s
     */
    @Deprecated
    Set<WorkItem> getWorkItems();

    WorkItem getWorkItem(long id);

    /**
     * This deletes all work items from the {@link WorkItemManager}'s cache
     */
    void clear();

    public void signalEvent(String type, Object event);

    public void signalEvent(String type, Object event, long processInstanceId);

    void dispose();

    void retryWorkItem( Long workItemID, Map<String, Object> params ) ;

    default void throwWorkItemHandlerNotFoundException( WorkItem workItem ) {
        throw new WorkItemHandlerNotFoundException( "Could not find work item handler for " + workItem.getName(), workItem.getName() );
    }
}

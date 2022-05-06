/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.process.workitems;

import java.util.Map;
import java.util.Set;

import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;

public interface InternalKogitoWorkItemManager extends org.drools.core.process.WorkItemManager, org.kie.kogito.internal.process.runtime.KogitoWorkItemManager {

    void internalExecuteWorkItem(InternalKogitoWorkItem workItem);

    void internalAddWorkItem(InternalKogitoWorkItem workItem);

    void internalAbortWorkItem(String id);

    void internalCompleteWorkItem(InternalKogitoWorkItem workItem);

    InternalKogitoWorkItem getWorkItem(String id);

    void internalRemoveWorkItem(String id);

    void signalEvent(String type, Object event, String processInstanceId);

    void retryWorkItem(String workItemID, Map<String, Object> params);

    Set<org.drools.core.process.WorkItem> getWorkItems();

    @Override
    default void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        registerWorkItemHandler(workItemName, (KogitoWorkItemHandler) handler);
    }

    @Override
    default void internalExecuteWorkItem(org.drools.core.process.WorkItem workItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void internalAddWorkItem(org.drools.core.process.WorkItem workItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void internalAbortWorkItem(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    default org.drools.core.process.WorkItem getWorkItem(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void retryWorkItem(Long aLong, Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }

}

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
 *
 */

package org.drools.core.process.instance.impl;

import org.drools.core.process.instance.TypedWorkItem;
import org.drools.core.process.instance.TypedWorkItemHandler;
import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * Decorates a {@link TypedWorkItemHandler} and exposes the {@link WorkItemHandler}
 * interface. Transparently converts between the TypedWorkItem
 * and WorkItem
 *
 */
public class UntypedWorkItemHandlerImpl<T extends TypedWorkItem<?, ?>> implements WorkItemHandler {

    private final TypedWorkItemHandler<T> workItemHandler;

    public UntypedWorkItemHandlerImpl(TypedWorkItemHandler<T> workItemHandler) {
        this.workItemHandler = workItemHandler;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        T typedWorkItem = workItemHandler.createTypedWorkItem();
        fillTyped(typedWorkItem, workItem);
        workItemHandler.executeWorkItem(typedWorkItem, manager);
        fillUntyped(workItem, typedWorkItem);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        T typedWorkItem = workItemHandler.createTypedWorkItem();
        fillTyped(typedWorkItem, workItem);
        workItemHandler.abortWorkItem(typedWorkItem, manager);
        fillUntyped(workItem, typedWorkItem);
    }

    private void fillTyped(T typedWorkItem, WorkItem workItem) {
        BeanMap.fillBean(typedWorkItem.getParameters(), workItem.getParameters());
        BeanMap.fillBean(typedWorkItem.getResults(), workItem.getResults());
    }

    private void fillUntyped(WorkItem workItem, T typedWorkItem) {
        BeanMap.fillMap(workItem.getParameters(), typedWorkItem.getParameters());
        BeanMap.fillMap(workItem.getResults(), typedWorkItem.getResults());
    }
}

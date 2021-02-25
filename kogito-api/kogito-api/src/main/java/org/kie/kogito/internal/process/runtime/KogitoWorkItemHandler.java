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
package org.kie.kogito.internal.process.runtime;

import org.kie.kogito.process.workitem.Transition;

public interface KogitoWorkItemHandler {

    /**
     * The given work item should be executed.
     *
     * @param workItem the work item that should be executed
     * @param manager the manager that requested the work item to be executed
     */
    void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager);

    /**
     * The given work item should be aborted.
     *
     * @param workItem the work item that should be aborted
     * @param manager the manager that requested the work item to be aborted
     */
    void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager);

    /**
     * Returns name that it should be registered with, by default simple class name;
     *
     * @return name that should this handler be registered with
     */
    default String getName() {
        return getClass().getSimpleName();
    }

    default void transitionToPhase(KogitoWorkItem workItem, KogitoWorkItemManager manager, Transition<?> transition) {
        throw new UnsupportedOperationException();
    }
}

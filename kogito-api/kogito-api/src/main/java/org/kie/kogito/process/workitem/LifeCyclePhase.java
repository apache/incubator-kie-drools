/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process.workitem;

import org.kie.api.runtime.process.WorkItem;

/**
 * Definition of the life cycle phase that work item can be connected to.
 *
 */
public interface LifeCyclePhase {

    /**
     * Returns unique id of this life cycle phase
     * 
     * @return phase id
     */
    String id();

    /**
     * Returns status associated with this life cycle phase
     * 
     * @return phase status
     */
    String status();

    /**
     * Returns if given state is the terminating phase (final state) for given work item
     * 
     * @return true if this is final phase otherwise false
     */
    boolean isTerminating();

    /**
     * Returns if given life cycle phase can be transitioned to this phase
     * 
     * @param phase phase to be transitioned from
     * @return true if phase can be transitioned from to this one otherwise false
     */
    boolean canTransition(LifeCyclePhase phase);

    /**
     * Optional extra work to be applied on work item upon transition to this phase
     * 
     * @param workitem work item that is being transitioned
     * @param transition actual transition
     */
    default void apply(WorkItem workitem, Transition<?> transition) {

    }
}

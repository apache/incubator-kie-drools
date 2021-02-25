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

import java.util.Date;

import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.process.workitem.Policy;

public interface KogitoWorkItem extends WorkItem {

    @Override
    @Deprecated
    long getId();

    String getStringId();

    /**
     * The id of the process instance that requested the execution of this
     * work item
     *
     * @return the id of the related process instance
     */
    String getProcessInstanceStringId();

    /**
     * Returns optional life cycle phase id associated with this work item
     * 
     * @return optional life cycle phase id
     */
    String getPhaseId();

    /**
     * Returns optional life cycle phase status associated with this work item
     * 
     * @return optional life cycle phase status
     */
    String getPhaseStatus();

    /**
     * Returns timestamp indicating the start date of this work item
     * 
     * @return start date
     */
    Date getStartDate();

    /**
     * Returns timestamp indicating the completion date of this work item
     * 
     * @return completion date
     */
    Date getCompleteDate();

    /**
     * The node instance that is associated with this
     * work item
     *
     * @return the related node instance
     */
    NodeInstance getNodeInstance();

    /**
     * The process instance that requested the execution of this
     * work item
     *
     * @return the related process instance
     */
    KogitoProcessInstance getProcessInstance();

    /**
     * Enforces given policies on this work item. It must false in case of any policy
     * violations.
     * 
     * @param policies optional policies to be enforced
     * @return return true if this work item can enforce all policies otherwise false
     */
    default boolean enforce(Policy<?>... policies) {
        return true;
    }
}
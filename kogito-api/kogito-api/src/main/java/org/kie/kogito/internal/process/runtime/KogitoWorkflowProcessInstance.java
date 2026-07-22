/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.internal.process.runtime;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.jobs.TimerDescription;
import org.kie.kogito.process.flexible.AdHocFragment;
import org.kie.kogito.process.flexible.Milestone;

public interface KogitoWorkflowProcessInstance extends WorkflowProcessInstance, KogitoProcessInstance, KogitoNodeInstanceContainer {

    /**
     * Returns start date of this process instance
     * 
     * @return actual start date
     */
    Date getStartDate();

    /**
     * Returns end date (either completed or aborted) of this process instance
     * 
     * @return actual end date
     */
    Date getEndDate();

    /**
     * Returns node definition id associated with node instance
     * that failed in case this process instance is in an error
     * 
     * @return node definition id of the failed node instance
     */
    String getNodeIdInError();

    String getNodeInstanceIdInError();

    /**
     * Returns error message associated with this process instance in case it is in an error
     * state. It will consists of
     * <ul>
     * <li>fully qualified class name of the root cause</li>
     * <li>error message of the root cause</li>
     * </ul>
     * 
     * @return error message
     */
    String getErrorMessage();

    /**
     * Returns the throwable originating the error, if available
     * 
     * @return
     */
    Optional<Throwable> getErrorCause();

    /**
     * Returns optional correlation key assigned to process instance
     * 
     * @return correlation key if present otherwise null
     */
    String getCorrelationKey();

    /**
     * Returns the list of Milestones and their status in the current process instances
     * 
     * @return Milestones defined in the process
     */
    Collection<Milestone> milestones();

    /**
     * Returns the timers associated with the process instance.
     *
     * @return a collection of {@link TimerDescription} instances representing the timers for the process instance, never null.
     */
    Collection<TimerDescription> timers();

    /**
     * @return AdHocFragments from the process instances
     */
    Collection<AdHocFragment> adHocFragments();

    Date getSlaDueDate();

}

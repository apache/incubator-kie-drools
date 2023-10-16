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
package org.kie.kogito.it.jobs;

import org.junit.jupiter.api.Test;

public abstract class BaseMultipleTimerInstancesQuarkusIT extends BaseMultipleTimerInstancesIT {

    private static final String MULTIPLE_TIMER_INSTANCES_EVENT_STATE_TIMEOUTS_URL = "/multiple_timer_instances_event_state_timeouts";
    private static final String MULTIPLE_TIMER_INSTANCES_EVENT_STATE_TIMEOUTS_GET_BY_ID_URL = MULTIPLE_TIMER_INSTANCES_EVENT_STATE_TIMEOUTS_URL + "/{id}";
    private static final String EMPTY_WORKFLOW_DATA = "{\"workflowdata\" : \"\"}";

    @Test
    void eventStateTimeouts() {
        executeInstancesAndEnsureTermination(MULTIPLE_TIMER_INSTANCES_EVENT_STATE_TIMEOUTS_URL,
                MULTIPLE_TIMER_INSTANCES_EVENT_STATE_TIMEOUTS_GET_BY_ID_URL,
                EMPTY_WORKFLOW_DATA);
    }
}

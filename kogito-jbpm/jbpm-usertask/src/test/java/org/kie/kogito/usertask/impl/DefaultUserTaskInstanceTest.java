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
package org.kie.kogito.usertask.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.usertask.UserTaskInstances;
import org.kie.kogito.usertask.lifecycle.UserTaskLifeCycle;
import org.kie.kogito.usertask.lifecycle.UserTaskState;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DefaultUserTaskInstanceTest {

    private UserTaskInstances instances;
    private DefaultUserTaskInstance userTaskInstance;

    @BeforeEach
    public void setup() {
        instances = mock(UserTaskInstances.class);
        userTaskInstance = new DefaultUserTaskInstance();
        userTaskInstance.setInstances(instances);
        userTaskInstance.setUserTaskLifeCycle(mock(UserTaskLifeCycle.class));
    }

    @Test
    public void testBatchUpdateCallsUpdateOnce() {
        userTaskInstance.batchUpdate(task -> {
            task.setTaskName("Test Task");
            task.setTaskDescription("Test Description");
            task.setTaskPriority("High");
            task.setActualOwner("testUser");
        });

        verify(instances, times(1)).update(userTaskInstance);
    }

    @Test
    public void testNonBatchUpdateCallsUpdateMultipleTimes() {
        userTaskInstance.setTaskName("Test Task");
        userTaskInstance.setTaskDescription("Test Description");
        userTaskInstance.setTaskPriority("High");
        userTaskInstance.setActualOwner("testUser");

        verify(instances, times(4)).update(userTaskInstance);
    }

    @Test
    public void testBatchUpdateCallsRemoveWhenTerminated() {
        userTaskInstance.setStatus(UserTaskState.of("Completed", UserTaskState.TerminationType.COMPLETED));

        userTaskInstance.batchUpdate(task -> {
            task.setTaskName("Completed Task");
        });

        verify(instances, times(1)).remove(userTaskInstance);
        verify(instances, never()).update(userTaskInstance);
    }
}

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

package org.acme;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.usertask.UserTaskEventListener;
import org.kie.kogito.usertask.events.UserTaskStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

// A test listener demonstrates transactional behavior of UserTaskEventListener, it will throw a RuntimeException when a task transitions to "Completed" status and verifies that the transaction rolls back, leaving the task in its previous state.

@ApplicationScoped
public class FailingUserTaskEventListener implements UserTaskEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailingUserTaskEventListener.class);
    private static final String PREFIX = ">>>>> [FAILING-LISTENER] ";

    @Inject
    @ConfigProperty(name = "app.listener.fail-on-complete", defaultValue = "false")
    boolean failOnComplete;

    @Override
    public void onUserTaskState(UserTaskStateEvent event) {
        String newStatus = event.getNewStatus() != null ? event.getNewStatus().getName() : null;

        LOGGER.info(PREFIX + "onUserTaskState: taskName={}, newStatus={}, failOnComplete={}",
                event.getUserTaskInstance().getTaskName(),
                newStatus,
                failOnComplete);

        if (failOnComplete && "Completed".equals(newStatus)) {
            throw new RuntimeException("Simulated external system failure. " +
                    "Transaction should rollback and task should remain in previous state.");
        }
    }
}

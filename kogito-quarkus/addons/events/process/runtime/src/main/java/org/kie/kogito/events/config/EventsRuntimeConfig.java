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
package org.kie.kogito.events.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(prefix = "kogito", name = "events", phase = ConfigPhase.RUN_TIME)
public class EventsRuntimeConfig {

    /**
     * Enable publishing processes instances events
     */
    @ConfigItem(name = "processinstances.enabled", defaultValue = "true")
    boolean processInstancesEventsEnabled;

    /**
     * Propagate errors for process instance emitter
     */
    @ConfigItem(name = "processinstances.errors.propagate", defaultValue = "false")
    boolean processInstancesPropagate;

    /**
     * Enable publishing processes definition events
     */
    @ConfigItem(name = "processdefinitions.enabled", defaultValue = "true")
    boolean processDefinitionEventsEnabled;

    /**
     * Propagate errors for process definition emitter
     */
    @ConfigItem(name = "processdefinitions.errors.propagate", defaultValue = "false")
    boolean processDefinitionPropagate;

    /**
     * Enable publishing user task instances events
     */
    @ConfigItem(name = "usertasks.enabled", defaultValue = "true")
    boolean userTasksEventsEnabled;

    /**
     * Propagate errors for user task emitter
     */
    @ConfigItem(name = "usertasks.errors.propagate", defaultValue = "false")
    boolean userTasksPropagate;

    public boolean isProcessInstancesEventsEnabled() {
        return processInstancesEventsEnabled;
    }

    public boolean isProcessDefinitionEventsEnabled() {
        return processDefinitionEventsEnabled;
    }

    public boolean isUserTasksEventsEnabled() {
        return userTasksEventsEnabled;
    }

    public boolean isProcessInstancesPropagateError() {
        return processInstancesPropagate;
    }

    public boolean isProcessDefinitionPropagateError() {
        return processDefinitionPropagate;
    }

    public boolean isUserTasksPropagateError() {
        return userTasksPropagate;
    }

}

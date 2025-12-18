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

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "kogito.events")
public interface EventsRuntimeConfig {

    /**
     * Enable publishing processes instances events
     */
    @WithName("processinstances.enabled")
    @WithDefault("true")
    boolean isProcessInstancesEventsEnabled();

    /**
     * Propagate errors for process instance emitter
     */
    @WithName("processinstances.errors.propagate")
    @WithDefault("false")
    boolean isProcessInstancesPropagateError();

    /**
     * Enable publishing processes definition events
     */
    @WithName("processdefinitions.enabled")
    @WithDefault("true")
    boolean isProcessDefinitionEventsEnabled();

    /**
     * Propagate errors for process definition emitter
     */
    @WithName("processdefinitions.errors.propagate")
    @WithDefault("false")
    boolean isProcessDefinitionsPropagateErrors();

    /**
     * Enable publishing user task instances events
     */
    @WithName("usertasks.enabled")
    @WithDefault("true")
    boolean isUserTasksEventsEnabled();

    /**
     * Propagate errors for user task emitter
     */
    @WithName("usertasks.errors.propagate")
    @WithDefault("false")
    boolean isUserTasksPropagateError();
}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 */
package org.jbpm.runtime.manager.api;

import org.jbpm.process.core.timer.GlobalSchedulerService;

/**
 * Marker interface to indicate that a given component provides a <code>SchedulerService</code>
 * This is especially important for RuntimeEnvironment implementations that might not provide such capabilities.
 */
public interface SchedulerProvider {

    /**
     * Returns fully configured instance of <code>SchedulerService</code> ready to be used/
     * @return <code>GlobalSchedulerService</code> instance configured according to environment needs
     */
    GlobalSchedulerService getSchedulerService();
}

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

package org.jbpm.process.core.timer;

import org.drools.core.time.impl.TimerJobInstance;

/**
 * Interceptor dedicated to <code>GlobalSchedulerService</code> to be able to react and 
 * optionally alter default behavior. Common case is to make a timer job scheduling transactional
 * but that's not the only possible case.
 */
public interface SchedulerServiceInterceptor {

    void internalSchedule(TimerJobInstance timerJobInstance);
}

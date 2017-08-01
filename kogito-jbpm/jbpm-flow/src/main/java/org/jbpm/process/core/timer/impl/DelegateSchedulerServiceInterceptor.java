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

package org.jbpm.process.core.timer.impl;

import org.drools.core.time.impl.TimerJobInstance;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.SchedulerServiceInterceptor;

/**
 * Simple delegate that is default implementation used if none other has been given.
 * It will just call the internalSchedule on the actual <code>GlobalSchedulerService</code>
 * so it does not introduce any new behavior.
 */
public class DelegateSchedulerServiceInterceptor implements SchedulerServiceInterceptor {

    protected GlobalSchedulerService delegate;
    
    public DelegateSchedulerServiceInterceptor(GlobalSchedulerService service) {
        this.delegate = service;
    }
    
    @Override
    public void internalSchedule(TimerJobInstance timerJobInstance) {
        this.delegate.internalSchedule(timerJobInstance);
    }

}

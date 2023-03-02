/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.test.util;

import org.kie.api.event.process.ProcessStartedEvent;

public class ProcessStartedCountDownProcessEventListener extends DefaultCountDownProcessEventListener {

    public ProcessStartedCountDownProcessEventListener() {
        super(1);
    }

    public ProcessStartedCountDownProcessEventListener(int threads) {
        super(threads);
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        countDown();
    }

}
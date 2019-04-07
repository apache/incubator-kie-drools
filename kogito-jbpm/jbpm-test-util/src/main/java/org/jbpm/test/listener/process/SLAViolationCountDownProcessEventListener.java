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

package org.jbpm.test.listener.process;

import java.util.concurrent.CountDownLatch;

import org.kie.api.event.process.SLAViolatedEvent;


public class SLAViolationCountDownProcessEventListener extends DefaultCountDownProcessEventListener {

    public SLAViolationCountDownProcessEventListener() {

    }

    public SLAViolationCountDownProcessEventListener(int threads) {
        super(threads);
    }

    public void reset(int threads) {
        this.latch = new CountDownLatch(threads);
    }

    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {
        countDown();
    }
    
    
}

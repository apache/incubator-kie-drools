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

package org.jbpm.process.workitem.core;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountDownProcessEventListener extends DefaultProcessEventListener {

    private static final Logger logger = LoggerFactory.getLogger(CountDownProcessEventListener.class);

    private String nodeName;
    private java.util.concurrent.CountDownLatch latch;

    public CountDownProcessEventListener(String nodeName,
                                         int threads) {
        this.nodeName = nodeName;
        this.latch = new java.util.concurrent.CountDownLatch(threads);
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        if (nodeName.equals(event.getNodeInstance().getNodeName())) {
            latch.countDown();
        }
    }

    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers for node {}",
                         nodeName);
        }
    }

    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut,
                        java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers for node {}",
                         nodeName);
        }
    }
}

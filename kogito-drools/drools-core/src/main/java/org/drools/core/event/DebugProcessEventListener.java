/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.event;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugProcessEventListener implements ProcessEventListener {

    protected static final transient Logger logger = LoggerFactory.getLogger(DebugProcessEventListener.class);

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        logger.info(event.toString());
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        logger.info(event.toString());
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        logger.info(event.toString());
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        logger.info(event.toString());
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        logger.info(event.toString());
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        logger.info(event.toString());
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        logger.info(event.toString());
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        logger.info(event.toString());
    }

    public void beforeProcessStarted(ProcessStartedEvent event) {
        logger.info(event.toString());
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        logger.info(event.toString());
    }

}

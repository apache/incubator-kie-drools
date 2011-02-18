/*
 * Copyright 2010 JBoss Inc
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

package org.drools.event;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;

public class DebugProcessEventListener implements ProcessEventListener {

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        System.err.println(event);
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.err.println(event);
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        System.err.println(event);
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        System.err.println(event);
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        System.err.println(event);
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        System.err.println(event);
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.err.println(event);
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        System.err.println(event);
    }

    public void beforeProcessStarted(ProcessStartedEvent event) {
        System.err.println(event);
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        System.err.println(event);
    }

}

/**
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

package org.drools.event.process;

public class DefaultProcessEventListener
    implements
    ProcessEventListener {

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        // intentionally left blank
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        // intentionally left blank
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        // intentionally left blank
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        // intentionally left blank
    }
    
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
    	// intentionally left blank
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        // intentionally left blank
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        // intentionally left blank
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        // intentionally left blank
    }

    public void beforeProcessStarted(ProcessStartedEvent event) {
        // intentionally left blank
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
    	// intentionally left blank
    }

}
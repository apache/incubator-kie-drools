/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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


import java.util.EventListener;

/**
 * A listener for events related to process instance execution.
 */
public interface ProcessEventListener
    extends
    EventListener {

	/**
	 * This listener method is invoked right before a process instance is being started.
	 * @param event
	 */
    void beforeProcessStarted(ProcessStartedEvent event);

	/**
	 * This listener method is invoked right after a process instance has been started.
	 * @param event
	 */
    void afterProcessStarted(ProcessStartedEvent event);

	/**
	 * This listener method is invoked right before a process instance is being completed (or aborted).
	 * @param event
	 */
    void beforeProcessCompleted(ProcessCompletedEvent event);

	/**
	 * This listener method is invoked right after a process instance has been completed (or aborted).
	 * @param event
	 */
    void afterProcessCompleted(ProcessCompletedEvent event);

	/**
	 * This listener method is invoked right before a node in a process instance is being triggered
	 * (which is when the node is being entered, for example when an incoming connection triggers it).
	 * @param event
	 */
    void beforeNodeTriggered(ProcessNodeTriggeredEvent event);

	/**
	 * This listener method is invoked right after a node in a process instance has been triggered
	 * (which is when the node was entered, for example when an incoming connection triggered it).
	 * @param event
	 */
    void afterNodeTriggered(ProcessNodeTriggeredEvent event);

	/**
	 * This listener method is invoked right before a node in a process instance is being left
	 * (which is when the node is completed, for example when it has performed the task it was
	 * designed for).
	 * @param event
	 */
    void beforeNodeLeft(ProcessNodeLeftEvent event);

	/**
	 * This listener method is invoked right after a node in a process instance has been left
	 * (which is when the node was completed, for example when it performed the task it was
	 * designed for).
	 * @param event
	 */
    void afterNodeLeft(ProcessNodeLeftEvent event);
    
	/**
	 * This listener method is invoked right before the value of a process variable is being changed.
	 * @param event
	 */
    void beforeVariableChanged(ProcessVariableChangedEvent event);

	/**
	 * This listener method is invoked right after the value of a process variable has been changed.
	 * @param event
	 */
    void afterVariableChanged(ProcessVariableChangedEvent event);

}

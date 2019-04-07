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

package org.jbpm.services.cdi.test.ext;

import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugTaskLifeCycleEventListener implements TaskLifeCycleEventListener {

	private static final Logger logger = LoggerFactory.getLogger(DebugTaskLifeCycleEventListener.class); 
	
	private static int eventCounter = 0;
	
	@Override
	public void beforeTaskActivatedEvent(TaskEvent event) {
		logger.info("beforeTaskActivatedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskClaimedEvent(TaskEvent event) {
		logger.info("beforeTaskClaimedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskSkippedEvent(TaskEvent event) {
		logger.info("beforeTaskSkippedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskStartedEvent(TaskEvent event) {
		logger.info("beforeTaskStartedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskStoppedEvent(TaskEvent event) {
		logger.info("beforeTaskStoppedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskCompletedEvent(TaskEvent event) {
		logger.info("beforeTaskCompletedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskFailedEvent(TaskEvent event) {
		logger.info("beforeTaskFailedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskAddedEvent(TaskEvent event) {
		logger.info("beforeTaskAddedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskExitedEvent(TaskEvent event) {
		logger.info("beforeTaskExitedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskReleasedEvent(TaskEvent event) {
		logger.info("beforeTaskReleasedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskResumedEvent(TaskEvent event) {
		logger.info("beforeTaskResumedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskSuspendedEvent(TaskEvent event) {
		logger.info("beforeTaskSuspendedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskForwardedEvent(TaskEvent event) {
		logger.info("beforeTaskForwardedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskDelegatedEvent(TaskEvent event) {
		logger.info("beforeTaskDelegatedEvent");
		eventCounter++;
	}

	@Override
	public void beforeTaskNominatedEvent(TaskEvent event) {
		logger.info("beforeTaskNominatedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskActivatedEvent(TaskEvent event) {
		logger.info("afterTaskActivatedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskClaimedEvent(TaskEvent event) {
		logger.info("afterTaskClaimedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskSkippedEvent(TaskEvent event) {
		logger.info("afterTaskSkippedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskStartedEvent(TaskEvent event) {
		logger.info("afterTaskStartedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskStoppedEvent(TaskEvent event) {
		logger.info("afterTaskStoppedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskCompletedEvent(TaskEvent event) {
		logger.info("afterTaskCompletedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskFailedEvent(TaskEvent event) {
		logger.info("afterTaskFailedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskAddedEvent(TaskEvent event) {
		logger.info("afterTaskAddedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskExitedEvent(TaskEvent event) {
		logger.info("afterTaskExitedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskReleasedEvent(TaskEvent event) {
		logger.info("afterTaskReleasedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskResumedEvent(TaskEvent event) {
		logger.info("afterTaskResumedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskSuspendedEvent(TaskEvent event) {
		logger.info("afterTaskSuspendedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskForwardedEvent(TaskEvent event) {
		logger.info("afterTaskForwardedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskDelegatedEvent(TaskEvent event) {
		logger.info("afterTaskDelegatedEvent");
		eventCounter++;
	}

	@Override
	public void afterTaskNominatedEvent(TaskEvent event) {
		logger.info("afterTaskNominatedEvent");
		eventCounter++;
	}

	public static int getEventCounter() {
		return eventCounter;
	}
	
	public static void resetEventCounter() {
		eventCounter = 0;
	}
}

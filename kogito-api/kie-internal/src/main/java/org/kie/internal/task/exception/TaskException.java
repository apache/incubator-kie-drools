/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates
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

package org.kie.internal.task.exception;

/**
 * Base class for all exceptions for the task related activities
 *
 * see org.jbpm.services.task.service.TaskServiceSession#addTask(org.jbpm.services.task.Task, ContentData)
 */
public abstract class TaskException extends RuntimeException {

	private static final long serialVersionUID = 2370182914623204842L;
	private boolean recoverable = true;
	
    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

	public boolean isRecoverable() {
		return recoverable;
	}

	public void setRecoverable(boolean recoverable) {
		this.recoverable = recoverable;
	}
}

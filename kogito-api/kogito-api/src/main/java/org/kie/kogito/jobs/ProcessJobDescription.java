/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

import org.kie.kogito.process.Process;

public class ProcessJobDescription implements JobDescription {

	public static final Integer DEFAULT_PRIORITY = 5;

	private final String id;

	private final ExpirationTime expirationTime;

	private final Integer priority;

	private String processId;

	private Process<?> process;

	private ProcessJobDescription(ExpirationTime expirationTime, Integer priority, String processId) {
		this.id = UUID.randomUUID().toString();
		this.expirationTime = requireNonNull(expirationTime);
		this.priority = requireNonNull(priority);
		this.processId = requireNonNull(processId);
	}

	public ProcessJobDescription(ExpirationTime expirationTime, Integer priority, Process<?> process) {
		this.id = UUID.randomUUID().toString();
		this.expirationTime = requireNonNull(expirationTime);
		this.priority = requireNonNull(priority);
		this.process = requireNonNull(process);
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public ExpirationTime expirationTime() {
		return expirationTime;
	}

	@Override
	public Integer priority() {
		return priority;
	}

	public String processId() {
		return processId;
	}

	public Process<?> process() {
		return process;
	}

    public static ProcessJobDescription of(ExpirationTime expirationTime, Process<?> process) {
        return new ProcessJobDescription(expirationTime, DEFAULT_PRIORITY, process);
    }

    public static ProcessJobDescription of(ExpirationTime expirationTime, String processId) {
        return of(expirationTime, DEFAULT_PRIORITY, processId);
    }

    public static ProcessJobDescription of(ExpirationTime expirationTime, Integer priority, String processId) {

        return new ProcessJobDescription(expirationTime, priority, processId);
    }
}

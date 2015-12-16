/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.examples.checklist.context;

import org.jbpm.examples.checklist.ChecklistContextConstraint;
import org.jbpm.examples.checklist.ChecklistItem;

public class ProcessIdContextConstraint implements ChecklistContextConstraint {
	
	private String processId;
	
	public ProcessIdContextConstraint(String processId) {
		if (processId == null) {
			throw new IllegalArgumentException("ProcessId cannot be null");
		}
		this.processId = processId;
	}
	
	public boolean acceptsTask(ChecklistItem item) {
		return processId.equals(item.getProcessId());
	}

}

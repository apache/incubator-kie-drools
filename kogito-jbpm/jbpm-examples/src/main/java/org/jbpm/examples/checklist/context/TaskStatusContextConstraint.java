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

import java.util.List;

import org.jbpm.examples.checklist.ChecklistContextConstraint;
import org.jbpm.examples.checklist.ChecklistItem;
import org.jbpm.examples.checklist.ChecklistItem.Status;

public class TaskStatusContextConstraint implements ChecklistContextConstraint {
	
	private List<Status> statusses;
	
	public TaskStatusContextConstraint(List<Status> statusses) {
		if (statusses == null || statusses.size() == 0) {
			throw new IllegalArgumentException("Statusses cannot be empty");
		}
		this.statusses = statusses;
	}
	
	public boolean acceptsTask(ChecklistItem item) {
		return statusses.contains(item.getStatus());
	}

}

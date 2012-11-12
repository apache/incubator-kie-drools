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

package org.jbpm.process.workitem.exec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.drools.process.instance.WorkItemHandler;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;

public class ExecWorkItemHandler implements WorkItemHandler {

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String command = (String) workItem.getParameter("Command");
		CommandLine commandLine = CommandLine.parse(command);
		DefaultExecutor executor = new DefaultExecutor();
		try {
			executor.execute(commandLine);
			manager.completeWorkItem(workItem.getId(), null);
		} catch (Throwable t) {
			t.printStackTrace();
			manager.abortWorkItem(workItem.getId());
		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// Do nothing, this work item cannot be aborted
	}

}

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

package org.jbpm.integration.console;

import org.jboss.bpm.console.server.integration.ManagementFactory;
import org.jboss.bpm.console.server.integration.ProcessManagement;
import org.jboss.bpm.console.server.integration.TaskManagement;
import org.jboss.bpm.console.server.integration.UserManagement;

public class DroolsFlowManagementFactory extends ManagementFactory {

	public ProcessManagement createProcessManagement() {
		return new DroolsFlowProcessManagement();
	}

	public TaskManagement createTaskManagement() {
		return new DroolsFlowTaskManagement();
	}

	public UserManagement createUserManagement() {
		return new DroolsFlowUserManagement();
	}

}

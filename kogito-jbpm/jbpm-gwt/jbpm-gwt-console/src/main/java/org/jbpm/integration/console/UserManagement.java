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

import java.util.ArrayList;
import java.util.List;

public class UserManagement implements org.jboss.bpm.console.server.integration.UserManagement {

	public List<String> getActorsForGroup(String groupName) {
		// TODO: fixme
		List<String> result = new ArrayList<String>();
		
		result.add("admin");
		return result;
	}

	public List<String> getGroupsForActor(String actorId) {
		// TODO: fixme
		List<String> result = new ArrayList<String>();
		result.add("admins");
		return result;
	}

}

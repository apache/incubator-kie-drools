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
package org.jbpm.task.service;

import java.util.ArrayList;
import java.util.List;

public class DefaultUserGroupCallbackImpl implements UserGroupCallback {

	public boolean existsUser(String userId) {
		// accept all by default
		return true;
	}

	public boolean existsGroup(String groupId) {
		// accept all by default
		return true;
	}
	
	public List<String> getGroupsForUser(String userId, List<String> groupIds,
			List<String> allExistingGroupIds) {
		if(groupIds != null) {
			List<String> retList = new ArrayList<String>(groupIds);
			if(allExistingGroupIds != null) {
				// merge
				for(String grp : allExistingGroupIds) {
					if(!retList.contains(grp)) {
						retList.add(grp);
					}
				}
			} 
			return retList;
		} else {
			// return empty list by default
			return new ArrayList<String>();
		}
	}
	
}

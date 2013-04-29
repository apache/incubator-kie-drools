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
package org.jbpm.services.task.identity;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.enterprise.inject.Alternative;

import org.jbpm.shared.services.cdi.Selectable;
import org.kie.internal.task.api.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alternative
@Selectable
public class JBossUserGroupCallbackImpl implements UserGroupCallback {
	
	private static final Logger logger = LoggerFactory.getLogger(JBossUserGroupCallbackImpl.class);

	private Map<String, List<String>> groupStore = new HashMap<String, List<String>>();
	private Set<String> allgroups = new HashSet<String>();
	
	public JBossUserGroupCallbackImpl() {
		this(System.getProperty("jbpm.user.group.mapping", "file:" + System.getProperty("jboss.server.config.dir") + "/roles.properties"));
	}
	
	public JBossUserGroupCallbackImpl(String location) {
		URL locationUrl = null;
		Properties userGroups = null;
		try {
			if (location.startsWith("classpath:")) {
				String stripedLocation = location.replaceFirst("classpath:", "");
				locationUrl = this.getClass().getResource(stripedLocation);
			} else {
				locationUrl = new URL(location);
			}
			
			userGroups = new Properties();
			userGroups.load(locationUrl.openStream());
		} catch (Exception e) {
			logger.error("Error when loading group information for callback from location: " + location, e);
		}
		
		init(userGroups);
	}
	
	public JBossUserGroupCallbackImpl(Properties userGroups) {
		
		init(userGroups);
	}
	
	protected void init(Properties userGroups) {
		if (userGroups == null) {
			throw new IllegalArgumentException("UserGroups properties cannot be null");
		}
		List<String> groups = null;
		Iterator<Object> it = userGroups.keySet().iterator();
		
		while (it.hasNext()) {
			String userId = (String) it.next();
			
			groups = Arrays.asList(userGroups.getProperty(userId, "").split(","));
			groupStore.put(userId, groups);
			allgroups.addAll(groups);
			
		}
		
		// always add Administrator if not already present
		if (!groupStore.containsKey("Administrator")) {
			groupStore.put("Administrator", Collections.<String> emptyList());
		}
	}

	public boolean existsUser(String userId) {
		return groupStore.containsKey(userId);
	}

	public boolean existsGroup(String groupId) {

		return allgroups.contains(groupId);
	}
	
	public List<String> getGroupsForUser(String userId, List<String> groupIds,
			List<String> allExistingGroupIds) {
		
		List<String> groups = groupStore.get(userId);
		return groups;
	}
	
}

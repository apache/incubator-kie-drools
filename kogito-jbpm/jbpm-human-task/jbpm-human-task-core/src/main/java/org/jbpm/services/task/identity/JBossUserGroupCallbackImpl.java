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
package org.jbpm.services.task.identity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.kie.internal.task.api.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JBossUserGroupCallbackImpl extends AbstractUserGroupInfo implements UserGroupCallback {
	
	private String separator;

	private static final Logger logger = LoggerFactory.getLogger(JBossUserGroupCallbackImpl.class);

	private static final String DEFAULT_PROPERTIES_LOCATION = "file:" + System.getProperty("jboss.server.config.dir") + "/roles.properties";

	private Map<String, List<String>> groupStore = new HashMap<String, List<String>>();
	private Set<String> allgroups = new HashSet<String>();
	
	//no no-arg constructor to prevent cdi from auto deploy
	public JBossUserGroupCallbackImpl(boolean activate) {
		this(System.getProperty("jbpm.user.group.mapping"));
	}
	
	public JBossUserGroupCallbackImpl(String location) {
		
		Properties userGroups = readProperties(location, DEFAULT_PROPERTIES_LOCATION);
		logger.debug("Loaded properties {}", userGroups);
		init(userGroups);
	}
	
	public JBossUserGroupCallbackImpl(Properties userGroups) {
		
		init(userGroups);
	}
	
	protected void init(Properties userGroups) {
		if (userGroups == null) {
			throw new IllegalArgumentException("UserGroups properties cannot be null");
		}
		this.separator = System.getProperty("org.jbpm.ht.user.separator", ",");
	        

		List<String> groups = null;
		Iterator<Object> it = userGroups.keySet().iterator();
		
		while (it.hasNext()) {
			String userId = (String) it.next();
			
			groups = Arrays.asList(userGroups.getProperty(userId, "").split(separator));
			groupStore.put(userId, groups);
			allgroups.addAll(groups);
			
		}
		
		// always add Administrator if not already present
		if (!groupStore.containsKey("Administrator")) {
			groupStore.put("Administrator", Collections.singletonList("Administrators"));
			allgroups.add("Administrators");
		}
	}

	public boolean existsUser(String userId) {
		return groupStore.containsKey(userId);
	}

	public boolean existsGroup(String groupId) {

		return allgroups.contains(groupId);
	}
	
	public List<String> getGroupsForUser(String userId) {
		
		List<String> groups = groupStore.get(userId);
		if( groups == null ) { 
		    groups = new ArrayList<String>(0);
		}
		return groups;
	}
	
}

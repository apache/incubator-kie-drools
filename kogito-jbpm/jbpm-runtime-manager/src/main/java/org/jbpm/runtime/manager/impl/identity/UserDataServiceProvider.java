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

package org.jbpm.runtime.manager.impl.identity;

import org.jbpm.services.task.identity.DBUserGroupCallbackImpl;
import org.jbpm.services.task.identity.DBUserInfoImpl;
import org.jbpm.services.task.identity.DefaultUserInfo;
import org.jbpm.services.task.identity.JAASUserGroupCallbackImpl;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.services.task.identity.LDAPUserGroupCallbackImpl;
import org.jbpm.services.task.identity.LDAPUserInfoImpl;
import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.jbpm.services.task.identity.PropertyUserInfoImpl;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;

/**
 * Provider of User/Group info services such as:
 * <ul>
 * 	<li>UserGroupCallback</li>
 * 	<li>UserInfo</li>
 * </ul>
 * It delivers various implementation depending on configuration of system properties where:
 * <ul>
 * 	<li>
 * org.jbpm.ht.callback - specify what implementation of user group callback will be selected,one of:
 * <ul>
 * 	<li>mvel - default mostly used for testing</li>
 * 	<li>ldap - ldap backed implementation - requires additional configuration via jbpm.usergroup.callback.properties file</li>
 * 	<li>db - data base backed implementation - requires additional configuration via jbpm.usergroup.callback.properties file</li>
 * 	<li>jaas - delegates to container to fetch information about user data</li>
 * 	<li>props - simple property based callback - requires additional file that will keep all information (users and groups)</li>
 * 	<li>custom - custom implementation that requires to have additional system property set (FQCN of the implementation) - org.jbpm.ht.custom.callback</li>
 * </ul>
 * </li>
 * 	<li>
 * org.jbpm.ht.userinfo - specify what implementation of UserInfo shall be used, one of:
 * <ul>
 * 	<li>ldap - backed by ldap - requires configuration via jbpm-user.info.properties file</li>
 * 	<li>db - backed by data base - requires configuration via jbpm-user.info.properties file</li>
 * 	<li>props - backed by simple property file</li>
 * 	<li>custom - custom implementation that requires to have additional system property set (FQCN of the implementation) - org.jbpm.ht.custom.userinfo</li>
 * </ul>
 * </li>
 * </ul>
 *
 */
public class UserDataServiceProvider {
	
	private static final String USER_CALLBACK_IMPL = System.getProperty("org.jbpm.ht.callback");
	private static final String USER_INFO_IMPL = System.getProperty("org.jbpm.ht.userinfo");
	
	private static final String CUSTOM_USER_CALLBACK_IMPL = System.getProperty("org.jbpm.ht.custom.callback");
	private static final String CUSTOM_USER_INFO_IMPL = System.getProperty("org.jbpm.ht.custom.userinfo");
	

	public static UserGroupCallback getUserGroupCallback() {
		
		UserGroupCallback callback = new MvelUserGroupCallbackImpl(true);
		if ("ldap".equalsIgnoreCase(USER_CALLBACK_IMPL)) {
			callback = new LDAPUserGroupCallbackImpl(true);
		} else if ("db".equalsIgnoreCase(USER_CALLBACK_IMPL)) {
			callback = new DBUserGroupCallbackImpl(true);
		} else if ("mvel".equalsIgnoreCase(USER_CALLBACK_IMPL)) {
			callback = new MvelUserGroupCallbackImpl(true);
		} else if ("props".equalsIgnoreCase(USER_CALLBACK_IMPL)) {
			callback = new JBossUserGroupCallbackImpl(true);
		} else if ("jaas".equalsIgnoreCase(USER_CALLBACK_IMPL)) {
			callback = new JAASUserGroupCallbackImpl(true);
		} else if ("custom".equalsIgnoreCase(USER_CALLBACK_IMPL)) {
			try {
				callback = (UserGroupCallback) Class.forName(CUSTOM_USER_CALLBACK_IMPL).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Unable to create instance of custom user group callback impl", e);
			}
		}
		
		return callback;
	}
	
	public static UserInfo getUserInfo() {
		UserInfo userInfo = new DefaultUserInfo(true);
		
		if ("ldap".equalsIgnoreCase(USER_INFO_IMPL)) {
			userInfo = new LDAPUserInfoImpl(true);
		} else if ("db".equalsIgnoreCase(USER_INFO_IMPL)) {
			userInfo = new DBUserInfoImpl(true);
		} else if ("props".equalsIgnoreCase(USER_INFO_IMPL)) {
			userInfo = new PropertyUserInfoImpl(true);
		} else if ("custom".equalsIgnoreCase(USER_INFO_IMPL)) {
			try {
				userInfo = (UserInfo) Class.forName(CUSTOM_USER_INFO_IMPL).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Unable to create instance of custom user info impl", e);
			}
		}
		
		return userInfo;
	}
}

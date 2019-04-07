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

package org.jbpm.services.task.identity.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebSphereUserGroupAdapter implements UserGroupAdapter {


    private static final Logger logger = LoggerFactory.getLogger(WebSphereUserGroupAdapter.class);
    private Object registry;

    public WebSphereUserGroupAdapter() {
        try {
            this.registry = InitialContext.doLookup("UserRegistry");
        } catch (NamingException e) {
            logger.info("Unable to look up UserRegistry in JNDI under key 'UserRegistry', disabling websphere adapter");
        }
    }

    @SuppressWarnings("rawtypes")
	@Override
    public List<String> getGroupsForUser(String userId) {
        List<String> roles = new ArrayList<String>();
        if (registry == null || userId == null || userId.isEmpty()) {
            return roles;
        }
        try {
            Method method = registry.getClass().getMethod("getGroupsForUser", new Class[]{String.class});
            List rolesIn = (List) method.invoke(registry, new Object[]{userId});
            if (rolesIn != null) {
                for (Object o : rolesIn) {
                    roles.add(  o.toString() );
                }
            }
        } catch (Exception e) {
            logger.error("Unable to get roles for user {} from registry due to {}", userId, e.getMessage(), e);
        }

        return roles;
    }
}
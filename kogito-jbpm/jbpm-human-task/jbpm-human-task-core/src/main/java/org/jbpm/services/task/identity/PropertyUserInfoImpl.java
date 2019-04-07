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

import java.util.Properties;

import org.kie.internal.task.api.UserInfo;

public class PropertyUserInfoImpl extends DefaultUserInfo implements UserInfo {

    //no no-arg constructor to prevent cdi from auto deploy
    public PropertyUserInfoImpl(boolean activate) {
        // use as no-arg constructor
        super(new Properties());
        try {
        	String propertiesLocation = System.getProperty("jbpm.user.info.properties");
	        Properties registryProps = readProperties(propertiesLocation, DEFAULT_USER_PROPS_LOCATION);
	        buildRegistry(registryProps);
        } catch (Exception e) {
            throw new IllegalStateException("Problem loading userinfo properties", e);
        }
    }
    
    /**
     * Constructs default UserInfo implementation to provide required information to the escalation handler.
     * following is the string for every organizational entity
     * entityId=email:locale:displayname:[member,member]
     * members are optional and should be given for group entities
     * @param registryProps
     */
    public PropertyUserInfoImpl(Properties registryProps) {
        super(registryProps);
    }
}
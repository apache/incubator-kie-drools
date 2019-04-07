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

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WeblogicUserGroupAdapter implements UserGroupAdapter {


    private static final Logger logger = LoggerFactory.getLogger(WeblogicUserGroupAdapter.class);
    private Class<?> webLogicSecurity;

    public WeblogicUserGroupAdapter() {
    	try {
            this.webLogicSecurity = Class.forName("weblogic.security.Security");
        } catch ( Exception e ) {
            logger.info( "Unable to find weblogic.security.Security, disabling weblogic adapter" );
        }
    }


	@Override
    public List<String> getGroupsForUser(String userId) {
        List<String> roles = new ArrayList<String>();
        if (webLogicSecurity == null || userId == null || userId.isEmpty()) {
            return roles;
        }
        try {
        	Method method = webLogicSecurity.getMethod("getCurrentSubject", new Class[]{});
            Subject wlsSubject = (Subject) method.invoke( null, new Object[]{ } );
            if ( wlsSubject != null ) {
                for ( java.security.Principal p : wlsSubject.getPrincipals() ) {
                    if (p.getClass().getName().indexOf("WLSGroup") != -1) {
                    	roles.add(  p.getName() );
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unable to get roles for user {} from subject due to {}", userId, e.getMessage(), e);
        }

        return roles;
    }
}
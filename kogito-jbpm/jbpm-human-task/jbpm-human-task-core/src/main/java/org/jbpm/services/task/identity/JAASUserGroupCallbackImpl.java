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

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;

import org.jbpm.services.task.identity.adapter.UserGroupAdapter;
import org.kie.api.task.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JAAS based implementation of user group callback dedicated when using LocalTaskService
 * in container such as JBoss AS. It relies on JACC api to collect information on currently
 * logged on user when querying for tasks. 
 * <br/>
 * JACC exposes following named context in PolicyContext:<br/>
 * <code>javax.security.auth.Subject.container</code>
 * <br/>
 * This returns <code>Subject</code> instance for currently authenticated user and next principals
 * will be examined to find instances of <code>Group</code> and with given rolePrincipleName (by default Roles).
 * <br/>
 * 
 * By default it works with JBoss Application Servers as it uses specific principal name to find the groups.
 * 
 *
 */
public class JAASUserGroupCallbackImpl extends AbstractUserGroupInfo implements UserGroupCallback {
	
	private static final Logger logger = LoggerFactory.getLogger(JAASUserGroupCallbackImpl.class);
	
	protected static final String DEFAULT_PROPERTIES_NAME = "classpath:/jbpm.usergroup.callback.properties";
	
	private ServiceLoader<UserGroupAdapter> ugAdapterServiceLoader = ServiceLoader.load(UserGroupAdapter.class);

	private static final ThreadLocal<UserGroupAdapter> externalUserGroupAdapterLocal = new ThreadLocal<UserGroupAdapter>();

	public static void addExternalUserGroupAdapter(UserGroupAdapter externalUserGroupAdapter) { 
	    if( externalUserGroupAdapterLocal.get() != null ) { 
	        UserGroupAdapter adapter = externalUserGroupAdapterLocal.get();
	        throw new IllegalStateException("The external UserGroupAdapter has already been set! "
	                + "(" + adapter.getClass().getName() + ")");
	    }
	    externalUserGroupAdapterLocal.set(externalUserGroupAdapter);
	}
	
	public static void clearExternalUserGroupAdapter() { 
	    externalUserGroupAdapterLocal.set(null);
	}
	
	private String rolePrincipleName = null;

	//no no-arg constructor to prevent cdi from auto deploy
	public JAASUserGroupCallbackImpl(boolean activate) {
		// use default JBoss AS role principle name
		this("Roles");
		
		String propertiesLocation = System.getProperty("jbpm.usergroup.callback.properties");
        
		Properties config = readProperties(propertiesLocation, DEFAULT_PROPERTIES_NAME);
		if (config != null) {
			this.rolePrincipleName = config.getProperty("jaas.role.principle.name", "Roles");
		}
 	}
	
	public JAASUserGroupCallbackImpl(String rolesPrincipleName) {
		this.rolePrincipleName = rolesPrincipleName;
	}
	
	public String getRolePrincipleName() {
		return rolePrincipleName;
	}

	public void setRolePrincipleName(String rolePrincipleName) {
		this.rolePrincipleName = rolePrincipleName;
	}

	public boolean existsUser(String userId) {
		// allows everything as there is no way to ask JAAS/JACC for users in the domain
		return true;
	}

	public boolean existsGroup(String groupId) {
		// allows everything as there is no way to ask JAAS/JACC for groups in the domain
		return true;
	}

	public List<String> getGroupsForUser(String userId) {
		List<String> roles = new ArrayList<String>();
        try {
            Subject subject = getSubjectFromContainer();
    
            if (subject != null) {
                Set<Principal> principals = subject.getPrincipals();
    
                if (principals != null) {
				    logger.debug("Adding roles from JAAS subject");
                    roles = new ArrayList<String>();
                    for (Principal principal : principals) {
                        if (principal instanceof Group  && rolePrincipleName.equalsIgnoreCase(principal.getName())) {
                            Enumeration<? extends Principal> groups = ((Group) principal).members();
                            
                            while (groups.hasMoreElements()) {
                                Principal groupPrincipal = (Principal) groups.nextElement();
                                roles.add(groupPrincipal.getName());
                            }
                            break;
                        }
                    }
                }
                
            } else {
				// use adapters
				for (UserGroupAdapter adapter : ugAdapterServiceLoader) {
				    logger.debug("Adding roles from UserGroupAdapter service ({})", adapter.getClass().getSimpleName());
					List<String> userRoles = adapter.getGroupsForUser(userId);
					if (userRoles != null) {
						roles.addAll(userRoles);
					}
				}
			}
        
            UserGroupAdapter adapter = externalUserGroupAdapterLocal.get();
            if( adapter != null ) { 
                logger.debug("Adding roles from external UserGroupAdapter ({})", adapter.getClass().getSimpleName());
                List<String> userRoles = adapter.getGroupsForUser(userId);
                if (userRoles != null) {
                    roles.addAll(userRoles);
                }
            }
        } catch (Exception e) {
            logger.error("Error when getting user roles for userid:" + userId, e);
        }
        return roles;
	}

	protected Subject getSubjectFromContainer() {
         try {
             return (Subject) PolicyContext.getContext( "javax.security.auth.Subject.container" );
         } catch (Exception e) {
             return null;
         }
     }

}

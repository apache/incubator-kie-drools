/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.tomcat;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.security.jacc.PolicyContextHandler;
import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.users.AbstractRole;
import org.apache.catalina.users.AbstractUser;
import org.apache.catalina.valves.ValveBase;

public class JACCValve extends ValveBase {
	
	private static Map<String, Subject> localCache = Collections.synchronizedMap(new HashMap<String, Subject>());
	
	private static ThreadLocal<Subject> subject = new ThreadLocal<Subject>();
	
	private static Subject get() {
		return subject.get();
	}
	
	public JACCValve() {
		try {
			PolicyContext.registerHandler("javax.security.auth.Subject.container", new PolicyContextHandler() {
				
				public boolean supports(String key) throws PolicyContextException {
					if ("javax.security.auth.Subject.container".equals(key)) {
						return true;
					}
					
					return false;
				}
				
				public String[] getKeys() throws PolicyContextException {
					return new String[]{"javax.security.auth.Subject.container"};
				}
				
				public Object getContext(String key, Object data)
						throws PolicyContextException {
					return JACCValve.get();
				}
			}, false);
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {
		String uri = request.getRequestURI();
		if (uri.endsWith("sid/invalidate")) {
			localCache.remove(request.getSession().getId());
		} else {
			subject.set(localCache.get(request.getSession().getId()));
		}
		// Perform the request
        getNext().invoke(request, response);

        if (uri.endsWith("user/roles") && request.getUserPrincipal() != null) {
			try {
					initJACC(request.getSession().getId(), request.getUserPrincipal());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
        subject.set(null);
		
		
	}
	
	private void initJACC(String sessionid, Principal principal) {

		Set<Principal> principals = new HashSet<Principal>();
		principals.add(principal);
		@SuppressWarnings("rawtypes")
		Iterator it = ((AbstractUser) principal).getRoles();
		if (it != null) {
			Group group = new Group() {
				
				private List<Principal> members = new ArrayList<Principal>();
				public String getName() {
					return "Roles";
				}
				
				public boolean removeMember(Principal user) {
					return members.remove(user);
				}
				
				public Enumeration<? extends Principal> members() {
					
					return Collections.enumeration(members);
				}
				
				public boolean isMember(Principal member) {
					return members.contains(member);
				}
				
				public boolean addMember(Principal user) {
					
					return members.add(user);
				}
			};
			while (it.hasNext()) {
				AbstractRole user = ((AbstractRole) it.next());
				group.addMember(user);
				
			}
			principals.add(group);
		}
		
		
		final Subject s = new Subject(false, principals , Collections.EMPTY_SET, Collections.EMPTY_SET);
		localCache.put(sessionid, s);
		
		
	}

}

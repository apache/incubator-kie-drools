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

package org.jbpm.kie.services.impl.security;

import java.util.List;

import org.jbpm.process.core.async.AsyncExecutionMarker;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.manager.SecurityManager;

public class IdentityRolesSecurityManager implements SecurityManager {
	
	private IdentityProvider identityProvider;	
	private List<String> requiredRoles;

	public IdentityRolesSecurityManager(IdentityProvider identityProvider, List<String> requiredRoles) {
		this.identityProvider = identityProvider;
		this.requiredRoles = requiredRoles;
	}
	
	@Override
	public void checkPermission() throws SecurityException {
		if (requiredRoles == null || requiredRoles.isEmpty()) {
			// all granted if roles are not defined
			return;
		}
		List<String> contextRoles = null;
		try {
			contextRoles = identityProvider.getRoles();
		} catch (Exception e) {
			// in case we cannot get hold of roles it most likely means that we are running in non secured context, e.g. timer
			return;
		}
		if (contextRoles != null) {		
			for (String requiredRole : requiredRoles) {
				if (contextRoles.contains(requiredRole) || identityProvider.hasRole(requiredRole)) {
					// role exists returns
					return;
				}
			}
		}
		// bypass security check if it's an async execution and not role information is available
		if (AsyncExecutionMarker.isAsync()) {
            // all granted if roles are not defined
            return;
        }
		throw new SecurityException("User " + identityProvider.getName() + " does not have permission to access this asset");
		
	}

}

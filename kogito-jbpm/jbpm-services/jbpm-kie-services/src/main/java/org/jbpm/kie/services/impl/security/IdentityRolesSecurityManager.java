package org.jbpm.kie.services.impl.security;

import java.util.List;

import org.jbpm.kie.services.api.IdentityProvider;
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
				if (contextRoles.contains(requiredRole)) {
					// role exists returns
					return;
				}
			}
		}
		throw new SecurityException("User " + identityProvider.getName() + " does not have permission to access this asset");
		
	}

}

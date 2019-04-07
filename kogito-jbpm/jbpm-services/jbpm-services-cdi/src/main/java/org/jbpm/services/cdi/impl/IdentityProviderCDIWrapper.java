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

package org.jbpm.services.cdi.impl;

import java.util.List;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Instance;

import org.jbpm.services.cdi.RequestScopedBackupIdentityProvider;
import org.kie.internal.identity.IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper to allow to use backup providers in case of given context is not available
 *
 */
public class IdentityProviderCDIWrapper implements IdentityProvider {
	

	private static final Logger logger = LoggerFactory.getLogger(IdentityProviderCDIWrapper.class);

	private IdentityProvider delegate;
	private Instance<RequestScopedBackupIdentityProvider> backupProviders;
	
	public IdentityProviderCDIWrapper(IdentityProvider identityProvider,
			Instance<RequestScopedBackupIdentityProvider> backupProviders) {
		this.delegate = identityProvider;
		this.backupProviders = backupProviders;
	}

	@Override
	public String getName() {
		String name = "unknown";
        try {
            name = delegate.getName();
            logger.debug( "Used original identity provider with user: {}", name);
        } catch (ContextNotActiveException e) {
        	if (!backupProviders.isUnsatisfied()) {
        		
        		for (RequestScopedBackupIdentityProvider provider : backupProviders) {
        			try {
		                name = provider.getName();
		                
		                if (name != null && !RequestScopedBackupIdentityProvider.UNKNOWN.equals(name)) {
		                	logger.debug( "Used backup identity provider {} with user: {}", provider, name);
		                	break;
		                }
        			} catch (ContextNotActiveException ex) {
        				name = RequestScopedBackupIdentityProvider.UNKNOWN;
        			}
        		}
        	}
        }

        return name;
	}
	
	@Override
	public List<String> getRoles() {
    	List<String> roles = null;
        try {
        	roles = delegate.getRoles();
            logger.debug( "Used original identity provider with roles: {}", roles);
        } catch (ContextNotActiveException e) {
            throw new IllegalStateException("Unable to get roles due to missing context", e);
        }

        return roles;
	}

	@Override
	public boolean hasRole(String role) {
		try {
			return delegate.hasRole(role);
		} catch (ContextNotActiveException e) {
	        throw new IllegalStateException("Unable to check role membership due to missing context", e);
	    }
	}

}

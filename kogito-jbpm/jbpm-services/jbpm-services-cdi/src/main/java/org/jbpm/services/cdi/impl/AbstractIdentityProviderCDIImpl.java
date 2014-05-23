/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.cdi.impl;

import java.util.List;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.services.cdi.RequestScopedBackupIdentityProvider;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classes that extends this one should not override <code>getName</code> method but only provide itself
 * as the delegate instance that will be called when needed  
 *
 */
public abstract class AbstractIdentityProviderCDIImpl implements IdentityProvider {
	

	private static final Logger logger = LoggerFactory.getLogger(AbstractIdentityProviderCDIImpl.class);

	private IdentityProvider delegate;
	private BeanManager beanManager;
	
	public AbstractIdentityProviderCDIImpl(BeanManager beanManager, IdentityProvider delegate) {
		this.beanManager = beanManager;
		this.delegate = delegate;
	}
		
	@Override
	public String getName() {
		return getIdentityName();
	}
	
	@Override
	public List<String> getRoles() {
		return getIdentityRoles();
	}


	/**
     * This method returns the identity of the user who initiated the command.
     * @return The identity
     */
    protected String getIdentityName() {
        String name = "unknown";
        try {
            name = delegate.getName();
            logger.debug( "Used original identity provider with user: {}", name);
        } catch (ContextNotActiveException e) {
            try {
	        	RequestScopedBackupIdentityProvider provider = getBackupIdentityProvider();
	            // if the beanManager field has NOT been set, then provider == null
	            if( provider != null ) { 
	                name = provider.getName();
	                logger.debug( "Used debug identity provider with user: {}", name);
	            }
            } catch (ContextNotActiveException ex) {
            	name = "unknown";
            }
        }

        return name;
    }
    
	/**
     * This method returns the identity of the user who initiated the command.
     * @return The identity
     */
    protected List<String> getIdentityRoles() {
    	List<String> roles = null;
        try {
        	roles = delegate.getRoles();
            logger.debug( "Used original identity provider with roles: {}", roles);
        } catch (ContextNotActiveException e) {
            throw new IllegalStateException("Unable to get roles due to missing context");
        }

        return roles;
    }
    
    /**
     * Sets the {@link BeanManager} field. 
     * </p>
     * This field is necessary in order to retrieve a {@link RequestScopedBackupIdentityProvider} bean from the CDI context. 
     * A {@link RequestScopedBackupIdentityProvider} bean is necessary when the a command is issued to the a {@link RuntimeEngine}
     * in a context or scope where HTTP is *not* used. The normal {@link IdentityProvider} bean is only available if HTTP is being 
     * used, because it relies on HTTP authorization mechanisms in order to get the user (See the UberfireIdentityProvider class).
     * 
     * @param beanManager A {@link BeanManager} instance
     */
    public void setBeanManager(BeanManager beanManager) {
        this.beanManager = beanManager;
    }
    
    /**
     * This retrieves a {@link RequestScopedBackupIdentityProvider} bean from the CDI (request scoped) context.
     * @return a {@link RequestScopedBackupIdentityProvider} instance
     */
    protected RequestScopedBackupIdentityProvider getBackupIdentityProvider() {
        Class<?> type = RequestScopedBackupIdentityProvider.class;
        logger.debug("Retrieving {} bean", type.getSimpleName() );
        if( beanManager != null ) { 
            final Bean<?> bean = beanManager.resolve(beanManager.getBeans(type));
            if (bean == null) {
                return null;
            }
            CreationalContext<?> cc = beanManager.createCreationalContext(null);
            return (RequestScopedBackupIdentityProvider) beanManager.getReference(bean, type, cc);
        } else { 
            return null;
        }
    }

}

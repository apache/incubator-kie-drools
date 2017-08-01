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

package org.jbpm.services.ejb.client.impl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jbpm.services.ejb.api.DefinitionServiceEJBRemote;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.jbpm.services.ejb.api.ProcessServiceEJBRemote;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote;
import org.jbpm.services.ejb.api.UserTaskServiceEJBRemote;
import org.jbpm.services.ejb.api.admin.ProcessInstanceMigrationServiceEJBRemote;
import org.jbpm.services.ejb.api.query.QueryServiceEJBRemote;
import org.jbpm.services.ejb.client.ClientServiceFactory;

/**
 * JBoss specific (AS 7 / EAP6 / WildFly 8) implementation of <code>ClientServiceFactory</code>
 * that is responsible for remote EJB look up.
 *
 */
public class JBossEJBClientServiceFactory implements ClientServiceFactory {
	
	private static final String NAME = "JBoss";
	
	private Map<Class<?>, String> beansMappedNames = new HashMap<Class<?>, String>();
	
	public JBossEJBClientServiceFactory() {
		beansMappedNames.put(ProcessServiceEJBRemote.class, "ProcessServiceEJBImpl!org.jbpm.services.ejb.api.ProcessServiceEJBRemote");
		beansMappedNames.put(DeploymentServiceEJBRemote.class, "DeploymentServiceEJBImpl!org.jbpm.services.ejb.api.DeploymentServiceEJBRemote");
		beansMappedNames.put(DefinitionServiceEJBRemote.class, "DefinitionServiceEJBImpl!org.jbpm.services.ejb.api.DefinitionServiceEJBRemote");
		beansMappedNames.put(RuntimeDataServiceEJBRemote.class, "RuntimeDataServiceEJBImpl!org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote");
		beansMappedNames.put(UserTaskServiceEJBRemote.class, "UserTaskServiceEJBImpl!org.jbpm.services.ejb.api.UserTaskServiceEJBRemote");
		beansMappedNames.put(QueryServiceEJBRemote.class, "QueryServiceEJBImpl!org.jbpm.services.ejb.api.query.QueryServiceEJBRemote");
		beansMappedNames.put(ProcessInstanceMigrationServiceEJBRemote.class, "ProcessInstanceMigrationServiceEJBImpl!org.jbpm.services.ejb.api.admin.ProcessInstanceMigrationServiceEJBRemote");
	}

	@SuppressWarnings("unchecked")
	public <T> T getService(String application, Class<T> serviceInterface) throws NamingException {
		
		final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
        String mappedName = beansMappedNames.get(serviceInterface);
        if (mappedName == null) {
        	throw new IllegalArgumentException("Unknown service interface " + serviceInterface.getName());
        }
        String jndi = "ejb:/" + application + "/" + mappedName;
        
		T bean = (T) context.lookup(jndi);
		return bean;
	}

	@Override
	public String getName() {
		return NAME;
	}
}

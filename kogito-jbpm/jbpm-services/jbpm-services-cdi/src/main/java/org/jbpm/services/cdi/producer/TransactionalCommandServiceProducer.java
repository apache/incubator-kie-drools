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

package org.jbpm.services.cdi.producer;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.cdi.Audit;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.internal.runtime.conf.DeploymentDescriptor;

public class TransactionalCommandServiceProducer {

    @Inject
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Produces
    public TransactionalCommandService produceCommandService() {
        return new TransactionalCommandService( emf );
    }
    
    @Produces
	@Audit
    public TransactionalCommandService produceAuditCommandService() {
    	DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");
    	DeploymentDescriptor descriptor = manager.getDefaultDescriptor();
    	if (!"org.jbpm.domain".equals(descriptor.getAuditPersistenceUnit())) {
    		return new TransactionalCommandService( EntityManagerFactoryManager.get().getOrCreate(descriptor.getAuditPersistenceUnit()) );
    	}
    	
        return new TransactionalCommandService( emf );
    }
}

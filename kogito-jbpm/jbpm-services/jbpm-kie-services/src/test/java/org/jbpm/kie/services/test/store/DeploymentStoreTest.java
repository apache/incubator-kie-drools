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

package org.jbpm.kie.services.test.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.runtime.manager.impl.deploy.TransientNamedObjectModel;
import org.jbpm.runtime.manager.impl.deploy.TransientObjectModel;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.runtime.conf.DeploymentDescriptor;

public class DeploymentStoreTest extends AbstractKieServicesBaseTest {

	private DeploymentStore store;
	
	@Before
	public void setup() {
		buildDatasource();
		emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
		
		store = new DeploymentStore();
		store.setCommandService(new TransactionalCommandService(emf));
	}
	
	@After
	public void cleanup() {
		close();
	}
	
	@Test
	public void testEnableAndGetActiveDeployments() {
		Collection<DeploymentUnit> enabled = store.getEnabledDeploymentUnits();
		assertNotNull(enabled);
		assertEquals(0, enabled.size());
		
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit("org.jbpm", "test", "1.0");
		
		store.enableDeploymentUnit(unit);
		
		enabled = store.getEnabledDeploymentUnits();
		assertNotNull(enabled);
		assertEquals(1, enabled.size());
	}
	
	@Test
	public void testEnableAndGetAndDisableActiveDeployments() {
		Collection<DeploymentUnit> enabled = store.getEnabledDeploymentUnits();
		assertNotNull(enabled);
		assertEquals(0, enabled.size());
		
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit("org.jbpm", "test", "1.0");
		
		store.enableDeploymentUnit(unit);
		
		enabled = store.getEnabledDeploymentUnits();
		assertNotNull(enabled);
		assertEquals(1, enabled.size());
		
		store.disableDeploymentUnit(unit);
		
		enabled = store.getEnabledDeploymentUnits();
		assertNotNull(enabled);
		assertEquals(0, enabled.size());
	}
	
	@Test
	public void testEnableAndGetByDateActiveDeployments() {
		Collection<DeploymentUnit> enabled = store.getEnabledDeploymentUnits();
		assertNotNull(enabled);
		assertEquals(0, enabled.size());
		Date date = new Date();
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit("org.jbpm", "test", "1.0");		
		store.enableDeploymentUnit(unit);
		
		unit = new KModuleDeploymentUnit("org.jbpm", "prod", "1.0");		
		store.enableDeploymentUnit(unit);
		
		Collection<DeploymentUnit> unitsEnabled = new HashSet<DeploymentUnit>();
		Collection<DeploymentUnit> unitsDisabled = new HashSet<DeploymentUnit>();
		Collection<DeploymentUnit> unitsActivated = new HashSet<DeploymentUnit>();
		Collection<DeploymentUnit> unitsDeactivated = new HashSet<DeploymentUnit>();
		
		store.getDeploymentUnitsByDate(date, unitsEnabled, unitsDisabled, unitsActivated, unitsDeactivated);
		assertNotNull(unitsEnabled);
		assertEquals(2, unitsEnabled.size());
		
		assertNotNull(unitsDisabled);
		assertEquals(0, unitsDisabled.size());
		
		date = new Date();		
		store.disableDeploymentUnit(unit);
		
		// verify
		unitsEnabled.clear();
		unitsDisabled.clear();
		unitsActivated.clear();
		unitsDeactivated.clear();
		
		store.getDeploymentUnitsByDate(date, unitsEnabled, unitsDisabled, unitsActivated, unitsDeactivated);
		assertNotNull(unitsEnabled);
		assertEquals(0, unitsEnabled.size());
		
		assertNotNull(unitsDisabled);
		assertEquals(1, unitsDisabled.size());
	}
	
	@Test
    public void testEnableAndGetActiveDeploymentsWithTransientNamedObject() {
        Collection<DeploymentUnit> enabled = store.getEnabledDeploymentUnits();
        assertNotNull(enabled);
        assertEquals(0, enabled.size());
        
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit("org.jbpm", "test", "1.0");
        
        DeploymentDescriptor descriptor = unit.getDeploymentDescriptor();
        if (descriptor == null) {
            descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        }
        // add transient named object model that should not be persisted
        descriptor.getBuilder()
        .addWorkItemHandler(new TransientNamedObjectModel("ejb", "async", "org.jbpm.executor.impl.wih.AsyncWorkItemHandler", 
                    new Object[]{"jndi:java:module/ExecutorServiceEJBImpl", "org.jbpm.executor.commands.PrintOutCommand"}))
        .addEventListener(new TransientObjectModel("ejb", "not.existing.listener"));
        
        unit.setDeploymentDescriptor(descriptor);
        
        store.enableDeploymentUnit(unit);
        
        enabled = store.getEnabledDeploymentUnits();
        assertNotNull(enabled);
        assertEquals(1, enabled.size());
        
        DeploymentUnit unitEnabled = enabled.iterator().next();
        assertTrue(unitEnabled instanceof KModuleDeploymentUnit);
        
        DeploymentDescriptor descriptorEnabled = ((KModuleDeploymentUnit) unitEnabled).getDeploymentDescriptor();
        assertNotNull(descriptorEnabled);
        
        assertEquals(0, descriptorEnabled.getWorkItemHandlers().size());
        assertEquals(0, descriptorEnabled.getEventListeners().size());
        
    }
}

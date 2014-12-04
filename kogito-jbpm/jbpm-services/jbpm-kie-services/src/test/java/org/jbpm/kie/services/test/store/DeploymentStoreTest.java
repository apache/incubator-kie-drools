package org.jbpm.kie.services.test.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.test.util.AbstractBaseTest;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeploymentStoreTest extends AbstractBaseTest {

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
}

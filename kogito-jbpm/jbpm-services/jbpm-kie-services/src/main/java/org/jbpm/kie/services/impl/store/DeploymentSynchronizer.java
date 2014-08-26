/*
 * Copyright 2014 JBoss Inc
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

package org.jbpm.kie.services.impl.store;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ListenerSupport;
import org.jbpm.services.api.model.DeploymentUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentSynchronizer implements DeploymentEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(DeploymentSynchronizer.class);
	
	public static final String DEPLOY_SYNC_INTERVAL = System.getProperty("org.jbpm.deploy.sync.int", "3");
	public static final boolean DEPLOY_SYNC_ENABLED = Boolean.parseBoolean(System.getProperty("org.jbpm.deploy.sync.enabled", "true"));
	
	private final Map<String, DeploymentUnit> entries = new ConcurrentHashMap<String, DeploymentUnit>();
	
	private DeploymentStore deploymentStore;
	private DeploymentService deploymentService;
	
	private Date lastSync = null;
	
	public boolean isActive() {
		return true;
	}
	

	public void setDeploymentStore(DeploymentStore deploymentStore) {
		this.deploymentStore = deploymentStore;
	}

	public void setDeploymentService(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
		((ListenerSupport)this.deploymentService).addListener(this);
	}

	public synchronized void synchronize() {
		try {
			Collection<DeploymentUnit> enabledSet = new HashSet<DeploymentUnit>();
			Collection<DeploymentUnit> disabledSet = new HashSet<DeploymentUnit>();
			if (lastSync == null) {
				// initial load
				enabledSet = deploymentStore.getEnabledDeploymentUnits();
			} else {
				deploymentStore.getDeploymentUnitsByDate(lastSync, enabledSet, disabledSet);
			}
			
			logger.debug("About to synchronize deployment units, found new enabled {}, found new disabled {}", enabledSet, disabledSet);
			if (enabledSet != null) {
				for (DeploymentUnit unit : enabledSet) {
					if (!entries.containsKey(unit.getIdentifier())) {
						try {
							logger.debug("New deployment unit to be deployed {}", unit);
							entries.put(unit.getIdentifier(), unit);
							deploymentService.deploy(unit);
						} catch (Exception e) {
							entries.remove(unit.getIdentifier());
							logger.warn("Deployment unit {} failed to deploy: {}", unit.getIdentifier(), e.getMessage());						
						}
					}
				}
			}
			
			if (disabledSet != null) {
				for (DeploymentUnit unit : disabledSet) {
					if (entries.containsKey(unit.getIdentifier())) {
						try {
							logger.debug("Existing deployment unit {} to be undeployed", unit.getIdentifier());
							entries.remove(unit.getIdentifier());
							deploymentService.undeploy(unit);
						} catch (Exception e) {
							logger.warn("Deployment unit {} failed to undeploy: {}", unit.getIdentifier(), e.getMessage(), e);
							entries.put(unit.getIdentifier(), unit);
							deploymentStore.markDeploymentUnitAsObsolete(unit);
						}
					}
				}
			}
		} catch (Throwable e) {
			logger.error("Error while synchronizing deployments: {}", e.getMessage());
		}
		// update last sync date
		this.lastSync = new Date();
	}
	
	@Override
	public void onDeploy(DeploymentEvent event) {
		if (event == null || event.getDeployedUnit() == null) {
			return;
		}
		DeploymentUnit unit = event.getDeployedUnit().getDeploymentUnit();
		if (!entries.containsKey(unit.getIdentifier())) {
			deploymentStore.enableDeploymentUnit(unit);
			// when successfully stored add it to local store
			entries.put(unit.getIdentifier(), unit);
			logger.info("Deployment unit {} stored successfully", unit.getIdentifier());
		}
		
	}

	@Override
	public void onUnDeploy(DeploymentEvent event) {
		if (event != null && event.getDeployedUnit() != null) {
			DeploymentUnit unit = event.getDeployedUnit().getDeploymentUnit();
			deploymentStore.disableDeploymentUnit(unit);
			entries.remove(unit.getIdentifier());
			logger.info("Deployment unit {} removed successfully", unit.getIdentifier());
		}
	}
	
}

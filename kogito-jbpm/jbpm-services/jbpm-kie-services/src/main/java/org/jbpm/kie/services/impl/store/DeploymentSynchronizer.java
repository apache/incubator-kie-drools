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

package org.jbpm.kie.services.impl.store;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ListenerSupport;
import org.jbpm.services.api.model.DeployedUnit;
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

	protected Class<?> targetExceptionClass;

	public DeploymentSynchronizer() {
        String clazz = System.getProperty("org.kie.constviol.exclass", "org.hibernate.exception.ConstraintViolationException");
        try {
            targetExceptionClass = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            logger.error("Optimistic locking exception class not found {}", clazz, e);
        }
    }

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
			Collection<DeploymentUnit> activatedSet = new HashSet<DeploymentUnit>();
			Collection<DeploymentUnit> deactivatedSet = new HashSet<DeploymentUnit>();
			Date timeOfSync = new Date();
			if (lastSync == null) {
				// initial load
				enabledSet = deploymentStore.getEnabledDeploymentUnits();
				deactivatedSet = deploymentStore.getDeactivatedDeploymentUnits();				
			} else {				
				deploymentStore.getDeploymentUnitsByDate(lastSync, enabledSet, disabledSet, activatedSet, deactivatedSet);
			}
			// update last sync date with time taken just before the query time
			this.lastSync = timeOfSync;

			logger.debug("About to synchronize deployment units, found new enabled {}, found new disabled {}", enabledSet, disabledSet);
			if (enabledSet != null) {
				for (DeploymentUnit unit : enabledSet) {
					if (!entries.containsKey(unit.getIdentifier()) && deploymentService.getDeployedUnit(unit.getIdentifier()) == null) {
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
					if (entries.containsKey(unit.getIdentifier()) && deploymentService.getDeployedUnit(unit.getIdentifier()) != null) {
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

			logger.debug("About to synchronize deployment units, found new activated {}, found new deactivated {}", activatedSet, deactivatedSet);
			if (activatedSet != null) {
				for (DeploymentUnit unit : activatedSet) {
					DeployedUnit deployed = deploymentService.getDeployedUnit(unit.getIdentifier());
					if (deployed != null && !deployed.isActive()) {
						deploymentService.activate(unit.getIdentifier());
					}
				}
			}

			if (deactivatedSet != null) {
				for (DeploymentUnit unit : deactivatedSet) {
					DeployedUnit deployed = deploymentService.getDeployedUnit(unit.getIdentifier());
					if (deployed != null && deployed.isActive()) {
						deploymentService.deactivate(unit.getIdentifier());
					}
				}
			}
		} catch (Throwable e) {
			logger.error("Error while synchronizing deployments: {}", e.getMessage(), e);
		}		
	}

	@Override
	public void onDeploy(DeploymentEvent event) {
		if (event == null || event.getDeployedUnit() == null) {
			return;
		}
		DeploymentUnit unit = event.getDeployedUnit().getDeploymentUnit();
		if (!entries.containsKey(unit.getIdentifier()) && event.getDeployedUnit().isActive()) {

			try {
				deploymentStore.enableDeploymentUnit(unit);
				// when successfully stored add it to local store
				entries.put(unit.getIdentifier(), unit);
				logger.info("Deployment unit {} stored successfully", unit.getIdentifier());
			} catch (Exception e) {
				if (isCausedByConstraintViolation(e)) {
					logger.info("Deployment {} already stored in deployment store", unit);
				} else {
					logger.error("Unable to store deployment {} in deployment store due to {}", unit, e.getMessage());
				}
			}
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


	@Override
	public void onActivate(DeploymentEvent event) {
		if (event != null && event.getDeployedUnit() != null) {
			DeploymentUnit unit = event.getDeployedUnit().getDeploymentUnit();
			deploymentStore.activateDeploymentUnit(unit);
			logger.info("Deployment unit {} activated successfully", unit.getIdentifier());
		}

	}


	@Override
	public void onDeactivate(DeploymentEvent event) {
		if (event != null && event.getDeployedUnit() != null) {
			DeploymentUnit unit = event.getDeployedUnit().getDeploymentUnit();
			deploymentStore.deactivateDeploymentUnit(unit);
			logger.info("Deployment unit {} deactivated successfully", unit.getIdentifier());
		}

	}

    protected boolean isCausedByConstraintViolation(Throwable throwable) {
        if (targetExceptionClass == null) {
            return false;
        }

        while (throwable != null) {
            if (targetExceptionClass.isAssignableFrom(throwable.getClass())
                    || SQLIntegrityConstraintViolationException.class.isAssignableFrom(throwable.getClass())) {
                return true;
            } else {
                throwable = throwable.getCause();
            }
        }

        return false;
    }

    public void clear() {
        this.entries.clear();
    }
}

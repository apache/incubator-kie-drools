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

package org.jbpm.kie.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;

import org.jbpm.kie.services.api.DeploymentIdResolver;
import org.jbpm.kie.services.impl.audit.ServicesAwareAuditEventBuilder;
import org.jbpm.kie.services.impl.security.IdentityRolesSecurityManager;
import org.jbpm.persistence.api.integration.EventManagerProvider;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ListenerSupport;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDeploymentService implements DeploymentService, ListenerSupport {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractDeploymentService.class);
    
    protected RuntimeManagerFactory managerFactory;     
    protected RuntimeDataService runtimeDataService;    
    protected EntityManagerFactory emf;    
    protected IdentityProvider identityProvider;
    
    protected Set<DeploymentEventListener> listeners = new LinkedHashSet<DeploymentEventListener>();
    
    
    @Override
    public void addListener(DeploymentEventListener listener) {
    	this.listeners.add(listener);
    }

    @Override
    public void removeListener(DeploymentEventListener listener) {
    	this.listeners.remove(listener);
    }
    
    @Override
    public Collection<DeploymentEventListener> getListeners() {
    	return Collections.unmodifiableSet(listeners);
    }
    
    protected Map<String, DeployedUnit> deploymentsMap = new ConcurrentHashMap<String, DeployedUnit>();
    
    @Override
    public void deploy(DeploymentUnit unit) {
        if (deploymentsMap.containsKey(unit.getIdentifier())) {
            throw new IllegalStateException("Unit with id " + unit.getIdentifier() + " is already deployed");
        }
    }
   
    public void notifyOnDeploy(DeploymentUnit unit, DeployedUnit deployedUnit){
    	DeploymentEvent event = new DeploymentEvent(unit.getIdentifier(), deployedUnit);
    	for (DeploymentEventListener listener : listeners) {
    		listener.onDeploy(event);
    	}    
    }
    public void notifyOnUnDeploy(DeploymentUnit unit, DeployedUnit deployedUnit){
    	DeploymentEvent event = new DeploymentEvent(unit.getIdentifier(), deployedUnit);
    	for (DeploymentEventListener listener : listeners) {
    		listener.onUnDeploy(event);
    	}
    }
    
    public void notifyOnActivate(DeploymentUnit unit, DeployedUnit deployedUnit){               
        DeploymentEvent event = new DeploymentEvent(unit.getIdentifier(), deployedUnit);
    	for (DeploymentEventListener listener : listeners) {
    		listener.onActivate(event);
    	}    
    }
    public void notifyOnDeactivate(DeploymentUnit unit, DeployedUnit deployedUnit){
        DeploymentEvent event = new DeploymentEvent(unit.getIdentifier(), deployedUnit);
    	for (DeploymentEventListener listener : listeners) {
    		listener.onDeactivate(event);
    	}    	
    }
    
    public void commonDeploy(DeploymentUnit unit, DeployedUnitImpl deployedUnit, RuntimeEnvironment environemnt, KieContainer kieContainer) {

        synchronized (this) {
        
            if (deploymentsMap.containsKey(unit.getIdentifier())) {
                DeployedUnit deployed = deploymentsMap.remove(unit.getIdentifier());
                RuntimeManager manager = deployed.getRuntimeManager();
                manager.close();
            }
            RuntimeManager manager = null;
            deploymentsMap.put(unit.getIdentifier(), deployedUnit);
            ((SimpleRuntimeEnvironment) environemnt).addToEnvironment("IdentityProvider", identityProvider);
            ((SimpleRuntimeEnvironment) environemnt).addToEnvironment("Active", deployedUnit.isActive());
            try {
                switch (unit.getStrategy()) {
            
                    case SINGLETON:
                        manager = managerFactory.newSingletonRuntimeManager(environemnt, unit.getIdentifier());
                        break;
                    case PER_REQUEST:
                        manager = managerFactory.newPerRequestRuntimeManager(environemnt, unit.getIdentifier());
                        break;
                        
                    case PER_PROCESS_INSTANCE:
                        manager = managerFactory.newPerProcessInstanceRuntimeManager(environemnt, unit.getIdentifier());
                        break;
                    case PER_CASE:
                        manager = managerFactory.newPerCaseRuntimeManager(environemnt, unit.getIdentifier());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid strategy " + unit.getStrategy());
                }  
                
                if (!deployedUnit.isActive()) {
                    ((InternalRuntimeManager)manager).deactivate();
                }                
                
                ((InternalRuntimeManager)manager).setKieContainer(kieContainer);
                deployedUnit.setRuntimeManager(manager);
                DeploymentDescriptor descriptor = ((InternalRuntimeManager)manager).getDeploymentDescriptor();
                List<String> requiredRoles = descriptor.getRequiredRoles(DeploymentDescriptor.TYPE_EXECUTE);
                if (requiredRoles != null && !requiredRoles.isEmpty()) {
                	((InternalRuntimeManager)manager).setSecurityManager(new IdentityRolesSecurityManager(identityProvider, requiredRoles));
                }
                notifyOnDeploy(unit, deployedUnit);
                
            } catch (Throwable e) {
                deploymentsMap.remove(unit.getIdentifier());
                if (manager != null) {
                	manager.close();
                }
                notifyOnUnDeploy(unit, deployedUnit);
                throw new RuntimeException(e);
            }
        }
        
    }
    
    @Override
    public void undeploy(DeploymentUnit unit) {
        List<Integer> states = new ArrayList<Integer>();
        states.add(ProcessInstance.STATE_ACTIVE);
        states.add(ProcessInstance.STATE_PENDING);
        states.add(ProcessInstance.STATE_SUSPENDED);
        Collection<ProcessInstanceDesc> activeProcesses = runtimeDataService.getProcessInstancesByDeploymentId(unit.getIdentifier(), states, new QueryContext());
        if (!activeProcesses.isEmpty()) {
            throw new IllegalStateException("Undeploy forbidden - there are active processes instances for deployment " 
                                            + unit.getIdentifier());
        }
        synchronized (this) {
            DeployedUnit deployed = deploymentsMap.remove(unit.getIdentifier());
            if (deployed != null) {
                RuntimeManager manager = deployed.getRuntimeManager();
                ((AbstractRuntimeManager)manager).close(true);
            }
            notifyOnUnDeploy(unit, deployed);
        }
    }

    @Override
    public RuntimeManager getRuntimeManager(String deploymentUnitId) {
        if (deploymentUnitId != null && deploymentsMap.containsKey(deploymentUnitId)) {
            return deploymentsMap.get(deploymentUnitId).getRuntimeManager();
        } else if (deploymentUnitId != null && deploymentUnitId.toLowerCase().contains("latest")) {
        	String matched = DeploymentIdResolver.matchAndReturnLatest(deploymentUnitId, deploymentsMap.keySet());

    		return deploymentsMap.get(matched).getRuntimeManager();

        }
        
        return null;
    }

	@Override
    public DeployedUnit getDeployedUnit(String deploymentUnitId) {
		DeployedUnit deployedUnit = null;
		if (deploymentsMap.containsKey(deploymentUnitId)) {
			deployedUnit = deploymentsMap.get(deploymentUnitId);
        } else if (deploymentUnitId != null && deploymentUnitId.toLowerCase().contains("latest")) {
        	String matched = DeploymentIdResolver.matchAndReturnLatest(deploymentUnitId, deploymentsMap.keySet());

        	deployedUnit = deploymentsMap.get(matched);        	
        }
		
        return deployedUnit;
    }
    
    public Map<String, DeployedUnit> getDeploymentsMap() {
        return deploymentsMap;
    }

    @Override
    public Collection<DeployedUnit> getDeployedUnits() {
        
        return Collections.unmodifiableCollection(deploymentsMap.values()) ;
    }


    public RuntimeManagerFactory getManagerFactory() {
        return managerFactory;
    }

    public void setManagerFactory(RuntimeManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    public RuntimeDataService getRuntimeDataService() {
        return runtimeDataService;
    }
    
    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }
    
    public void setIdentityProvider(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}
	
	protected AuditEventBuilder setupAuditLogger(IdentityProvider identityProvider, String deploymentUnitId) { 
	       
        ServicesAwareAuditEventBuilder auditEventBuilder = new ServicesAwareAuditEventBuilder();
        auditEventBuilder.setIdentityProvider(identityProvider);
        auditEventBuilder.setDeploymentUnitId(deploymentUnitId);      
        
        return auditEventBuilder;
    }
	
    @Override
    public boolean isDeployed(String deploymentUnitId) {
        return deploymentsMap.containsKey(deploymentUnitId);
    }
    
    public void shutdown() {
    	Collection<DeployedUnit> deployedUnits = getDeployedUnits();
    	
    	for (DeployedUnit deployed : deployedUnits) {
    		try {
    			deployed.getRuntimeManager().close();
    		} catch (Exception e) {
    			logger.warn("Error encountered while shutting down deplyment {} due to {}", 
    					deployed.getDeploymentUnit().getIdentifier(), e.getMessage());
    		}
    	}
    	deploymentsMap.clear();
    	
    	EventManagerProvider.getInstance().get().close();
    }
	
}

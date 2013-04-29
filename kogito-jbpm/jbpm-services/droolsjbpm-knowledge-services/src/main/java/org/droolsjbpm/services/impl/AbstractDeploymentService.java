package org.droolsjbpm.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.droolsjbpm.services.api.DeployedUnit;
import org.droolsjbpm.services.api.DeploymentService;
import org.droolsjbpm.services.api.DeploymentUnit;
import org.droolsjbpm.services.impl.event.Deploy;
import org.droolsjbpm.services.impl.event.DeploymentEvent;
import org.droolsjbpm.services.impl.event.Undeploy;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;

public abstract class AbstractDeploymentService implements DeploymentService {

    @Inject
    private RuntimeManagerFactory managerFactory; 
    @Inject
    @Deploy
    protected Event<DeploymentEvent> deploymentEvent;
    @Inject
    @Undeploy
    protected Event<DeploymentEvent> undeploymentEvent;
    
    protected Map<String, DeployedUnit> deploymentsMap = new ConcurrentHashMap<String, DeployedUnit>();
    
    public void commonDdeploy(DeploymentUnit unit, DeployedUnitImpl deployedUnit, RuntimeEnvironment environemnt) {

        synchronized (this) {
        
            if (deploymentsMap.containsKey(unit.getIdentifier())) {
                DeployedUnit deployed = deploymentsMap.remove(unit.getIdentifier());
                RuntimeManager manager = deployed.getRuntimeManager();
                manager.close();
            }
            RuntimeManager manager = null;
            deploymentsMap.put(unit.getIdentifier(), deployedUnit);
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
                    default:
                        throw new IllegalArgumentException("Invalid strategy " + unit.getStrategy());
                }            
                deployedUnit.setRuntimeManager(manager);
            } catch (Exception e) {
                deploymentsMap.remove(unit.getIdentifier());
                throw new RuntimeException(e);
            }
        }
        if (deploymentEvent != null) {
            deploymentEvent.fire(new DeploymentEvent(unit.getIdentifier()));
        }
        
    }
    
    @Override
    public void undeploy(DeploymentUnit unit) {
        synchronized (this) {
            DeployedUnit deployed = deploymentsMap.remove(unit.getIdentifier());
            if (deployed != null) {
                RuntimeManager manager = deployed.getRuntimeManager();
                manager.close();
            }
            if (undeploymentEvent != null) {
                undeploymentEvent.fire(new DeploymentEvent(unit.getIdentifier()));
            }
        }
    }

    @Override
    public RuntimeManager getRuntimeManager(String deploymentUnitId) {
        if (deploymentsMap.containsKey(deploymentUnitId)) {
            return deploymentsMap.get(deploymentUnitId).getRuntimeManager();
        }
        
        return null;
    }

    @Override
    public DeployedUnit getDeployedUnit(String deploymentUnitId) {
        if (deploymentsMap.containsKey(deploymentUnitId)) {
            return deploymentsMap.get(deploymentUnitId);
        }
        
        return null;
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
}

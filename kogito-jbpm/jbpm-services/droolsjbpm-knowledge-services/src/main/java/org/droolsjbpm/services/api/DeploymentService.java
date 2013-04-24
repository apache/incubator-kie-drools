package org.droolsjbpm.services.api;

import java.util.Collection;

import org.kie.api.runtime.manager.RuntimeManager;

public interface DeploymentService {

    void deploy(DeploymentUnit unit);
    
    void undeploy(DeploymentUnit unit);
    
    RuntimeManager getRuntimeManager(String deploymentUnitId);
    
    DeployedUnit getDeployedUnit(String deploymentUnitId);
    
    Collection<DeployedUnit> getDeployedUnits();
}

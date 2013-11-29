package org.jbpm.kie.services.impl.event;

import org.kie.internal.deployment.DeployedUnit;

public class DeploymentEvent {
    
    private String deploymentId;
    private DeployedUnit deployedUnit;
    
    public DeploymentEvent(String deploymentId, DeployedUnit deployedUnit) {
        this.deployedUnit = deployedUnit;
        this.deploymentId = deploymentId;
    }

    public DeployedUnit getDeployedUnit() {
        return deployedUnit;
    }

    public void setDeployedUnit(DeployedUnit deployedUnit) {
        this.deployedUnit = deployedUnit;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    
}

package org.droolsjbpm.services.impl.event;

public class DeploymentEvent {
    
    private String deploymentId;
    
    public DeploymentEvent(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    
    public String getDeploymentId() {
        return deploymentId;
    }
    
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
}

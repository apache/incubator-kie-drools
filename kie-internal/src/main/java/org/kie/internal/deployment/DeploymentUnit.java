package org.kie.internal.deployment;

public interface DeploymentUnit {
    
    public enum RuntimeStrategy {
        SINGLETON,
        PER_REQUEST,
        PER_PROCESS_INSTANCE;
    }
    
    String getIdentifier();
    
    RuntimeStrategy getStrategy();
}

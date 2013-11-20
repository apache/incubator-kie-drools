package org.jbpm.kie.services.api;

import java.util.Collection;

import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.runtime.manager.RuntimeManager;

public interface DeployedUnit {

    DeploymentUnit getDeploymentUnit();
    
    String getDeployedAssetLocation(String assetId);
    
    Collection<ProcessDesc> getDeployedAssets(); 
    
    Collection<String> getDeployedClassNames(); 
    
    RuntimeManager getRuntimeManager();
}

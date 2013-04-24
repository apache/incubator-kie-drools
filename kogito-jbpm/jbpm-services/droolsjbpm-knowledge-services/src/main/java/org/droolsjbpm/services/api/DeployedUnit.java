package org.droolsjbpm.services.api;

import java.util.Collection;

import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.kie.api.runtime.manager.RuntimeManager;

public interface DeployedUnit {

    DeploymentUnit getDeploymentUnit();
    
    String getDeployedAssetLocation(String assetId);
    
    Collection<ProcessDesc> getDeployedAssets(); 
    
    RuntimeManager getRuntimeManager();
}

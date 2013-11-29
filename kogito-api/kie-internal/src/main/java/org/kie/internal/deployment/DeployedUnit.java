package org.kie.internal.deployment;

import java.util.Collection;

import org.kie.api.runtime.manager.RuntimeManager;

public interface DeployedUnit {

    DeploymentUnit getDeploymentUnit();
    
    String getDeployedAssetLocation(String assetId);
    
    Collection<DeployedAsset> getDeployedAssets(); 
    
    Collection<String> getDeployedClassNames(); 
    
    RuntimeManager getRuntimeManager();
}

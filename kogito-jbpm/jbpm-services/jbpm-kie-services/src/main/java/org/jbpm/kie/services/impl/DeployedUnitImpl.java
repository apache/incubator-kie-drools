package org.jbpm.kie.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.runtime.manager.RuntimeManager;

public class DeployedUnitImpl implements DeployedUnit {
    
    private DeploymentUnit unit;
    private RuntimeManager manager;
    
    private Map<String, ProcessDesc> assets = new HashMap<String, ProcessDesc>();
    private Set<String> classes = new HashSet<String>();
    
    public DeployedUnitImpl(DeploymentUnit unit) {
        this.unit = unit;
    }

    @Override
    public DeploymentUnit getDeploymentUnit() {
        return this.unit;
    }

    @Override
    public String getDeployedAssetLocation(String assetId) {
        return this.assets.get(assetId).getOriginalPath();
    }

    @Override
    public RuntimeManager getRuntimeManager() {
        return this.manager;
    }
    
    public void addAssetLocation(String assetId, ProcessDesc processAsset) {
        this.assets.put(assetId, processAsset);
    }
    
    public void addClassName(String className) {
        this.classes.add(className);
    }
    
    public void setRuntimeManager(RuntimeManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException("RuntimeManager already exists");
        }
        this.manager = manager;
    }

    @Override
    public Collection<ProcessDesc> getDeployedAssets() {
        return Collections.unmodifiableCollection(assets.values());
    }

    @Override
    public Collection<String> getDeployedClassNames() {
        return Collections.unmodifiableCollection(classes);
    }
}

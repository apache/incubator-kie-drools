package org.jbpm.kie.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.deployment.DeployedAsset;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentUnit;

public class DeployedUnitImpl implements DeployedUnit {
    
    private DeploymentUnit unit;
    private RuntimeManager manager;
    
    private Map<String, DeployedAsset> assets = new HashMap<String, DeployedAsset>();
    private Set<Class<?>> classes = new CopyOnWriteArraySet<Class<?>>();
    
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
    
    public void addAssetLocation(String assetId, ProcessAssetDesc processAsset) {
        this.assets.put(assetId, processAsset);
    }
    
    public void addClass(Class<?> kModuleClass) {
        this.classes.add(kModuleClass);
    }
    
    public void setRuntimeManager(RuntimeManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException("RuntimeManager already exists");
        }
        this.manager = manager;
    }

    @Override
    public Collection<DeployedAsset> getDeployedAssets() {
        return Collections.unmodifiableCollection(assets.values());
    }

    @Override
    public Collection<Class<?>> getDeployedClasses() {
        return Collections.unmodifiableCollection(classes);
    }

}

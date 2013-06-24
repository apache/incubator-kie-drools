package org.jbpm.kie.services.impl;

import org.drools.core.util.StringUtils;
import org.jbpm.kie.services.api.DeploymentUnit;

public class KModuleDeploymentUnit implements DeploymentUnit {

    private String artifactId;
    private String groupId;
    private String version;
    private String kbaseName;
    private String ksessionName;
    
    private RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;
    
    
    public KModuleDeploymentUnit(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
    
    public KModuleDeploymentUnit(String groupId, String artifactId, 
            String version, String kbaseName, String ksessionName) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
        this.kbaseName = kbaseName;
        this.ksessionName = ksessionName;
    }

    public KModuleDeploymentUnit(String groupId, String artifactId, 
            String version, String kbaseName, String ksessionName, String strategy) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
        this.kbaseName = kbaseName;
        this.ksessionName = ksessionName;
        this.strategy = RuntimeStrategy.valueOf(strategy);
    }

    @Override
    public String getIdentifier() {
        String id = getGroupId()+":"+getArtifactId()+":"+getVersion();
        if (!StringUtils.isEmpty(kbaseName)) {
            id = id.concat(":" + kbaseName);
        }
        if (!StringUtils.isEmpty(ksessionName)) {
            id = id.concat(":" + ksessionName);
        }
        return id;
    }

    @Override
    public RuntimeStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RuntimeStrategy strategy) {
        this.strategy = strategy;
    }
    
    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKsessionName() {
        return ksessionName;
    }

    public void setKsessionName(String ksessionName) {
        this.ksessionName = ksessionName;
    }

    public String getKbaseName() {
        return kbaseName;
    }

    public void setKbaseName(String kbaseName) {
        this.kbaseName = kbaseName;
    }

    @Override
    public String toString() {
        return getIdentifier() + " [strategy=" + strategy + "]";
    }

}

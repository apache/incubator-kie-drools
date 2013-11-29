package org.kie.internal.deployment;

public interface DeployedAsset {

    public String getId();

    public String getName();

    public String getVersion();

    public String getKnowledgeType();
    
    public String getOriginalPath();
    
}

package org.jbpm.kie.services.impl;

import org.jbpm.kie.services.api.DeploymentUnit;

public class VFSDeploymentUnit implements DeploymentUnit {

    private String identifier;
    private String repositoryScheme = "default";
    private String repositoryAlias;
    private String repositoryFolder;
    
    private RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;
    
    public VFSDeploymentUnit(String identifier, String repositoryAlias, String repositoryFolder) {
        this.identifier = identifier;
        this.repositoryAlias = repositoryAlias;
        this.repositoryFolder = repositoryFolder;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public String getRepositoryAlias() {
        return repositoryAlias;
    }

    public void setRepositoryAlias(String repositoryAlias) {
        this.repositoryAlias = repositoryAlias;
    }

    public String getRepositoryFolder() {
        return repositoryFolder;
    }

    public void setRepositoryFolder(String repositoryFolder) {
        this.repositoryFolder = repositoryFolder;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public RuntimeStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RuntimeStrategy strategy) {
        this.strategy = strategy;
    }
    
    public String getRepository() {
        if (repositoryAlias!= null && !repositoryAlias.equals("")) {
            String repo = this.repositoryScheme + "://" + this.repositoryAlias;
            if (!this.repositoryFolder.startsWith("/")) {
                repo +="/";
            }
            return repo;
        }
        return "";
    }

    public String getRepositoryScheme() {
        return repositoryScheme;
    }

    public void setRepositoryScheme(String repositoryScheme) {
        this.repositoryScheme = repositoryScheme;
    }

}

package org.drools.io.impl;

import java.util.Collection;
import java.util.Collections;

import org.drools.ChangeSet;
import org.drools.io.Resource;

public class ChangeSetImpl implements ChangeSet {
    private Collection<Resource> resourcesRemoved = Collections.<Resource>emptyList();
    private Collection<Resource> resourcesAdded = Collections.<Resource>emptyList();
    private Collection<Resource> resourcesModified = Collections.<Resource>emptyList();    

    //Map of removed kdefinitions. The key is the resource and the string is
    //the knowledgeDefinition's name.
    private Collection<String>  knowledgeDefinitionsRemoved = Collections.<String>emptyList();
    
    public ChangeSetImpl() {
        
    }
    
    public void setResourcesRemoved(Collection<Resource> resourcesRemoved) {
        this.resourcesRemoved = resourcesRemoved;
    }

    public Collection<Resource> getResourcesRemoved() {
        return resourcesRemoved;
    }
    
    public void setResourcesAdded(Collection<Resource> resourcesAdded) {
        this.resourcesAdded = resourcesAdded;
    }    

    public Collection<Resource> getResourcesAdded() {
        return resourcesAdded;
    }    

    public Collection<Resource> getResourcesModified() {
        return resourcesModified;
    }

    public void setResourcesModified(Collection<Resource> resourcesModified) {
        this.resourcesModified = resourcesModified;
    }

    public Collection<String> getKnowledgeDefinitionsRemoved() {
        return knowledgeDefinitionsRemoved;
    }

    public void setKnowledgeDefinitionsRemoved(Collection<String> knowledgeDefinitionsRemoved) {
        this.knowledgeDefinitionsRemoved = knowledgeDefinitionsRemoved;
    }

    
}

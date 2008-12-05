package org.drools;

import java.util.Collection;

import org.drools.definition.KnowledgeDefinition;
import org.drools.io.Resource;

public interface ChangeSet {
    public Collection<Resource> getResourcesRemoved();


    public Collection<Resource> getResourcesAdded();
    
    public Collection<Resource> getResourcesModified();


    //public Collection<KnowledgeDefinition> getKnowledgeDefinitionsRemoved();

}

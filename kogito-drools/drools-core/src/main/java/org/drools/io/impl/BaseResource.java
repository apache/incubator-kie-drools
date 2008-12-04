package org.drools.io.impl;

import org.drools.builder.KnowledgeType;

public abstract class BaseResource {
    private boolean fromDirectory;
    private KnowledgeType KnowledgeType;
    
    public boolean isFromDirectory() {
        return this.fromDirectory;
    }

    public void setFromDirectory(boolean fromDirectory) {
        this.fromDirectory = fromDirectory;
    }  
    

    public void setKnowledgeType(KnowledgeType knowledgeType) {
        this.KnowledgeType = knowledgeType;
    }
    
    
    public KnowledgeType getKnowledgeType() {
        return this.KnowledgeType;
    }


}
